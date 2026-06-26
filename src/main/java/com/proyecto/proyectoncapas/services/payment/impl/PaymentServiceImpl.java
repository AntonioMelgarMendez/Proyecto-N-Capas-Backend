package com.proyecto.proyectoncapas.services.payment.impl;

import com.proyecto.proyectoncapas.dto.response.BookingExtensionResponseDTO;
import com.proyecto.proyectoncapas.entities.ExtensionRequest;
import com.proyecto.proyectoncapas.entities.Payment;
import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.exception.InvalidPaymentStateException;
import com.proyecto.proyectoncapas.exception.PaymentNotFoundException;
import com.proyecto.proyectoncapas.exception.RefundProcessingException;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.ExtensionRequestRepository;
import com.proyecto.proyectoncapas.repository.PaymentRepository;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.services.payment.PaymentService;
import com.proyecto.proyectoncapas.services.reservation.ReservationService;
import com.proyecto.proyectoncapas.utils.enums.ExtensionRequestStatus;
import com.proyecto.proyectoncapas.utils.enums.PaymentType;
import com.proyecto.proyectoncapas.utils.enums.ReservationStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private static final List<String> REFUNDABLE_STATUSES = List.of("COMPLETED", "PARTIALLY_REFUNDED");

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final ExtensionRequestRepository extensionRequestRepository;
    private final ReservationService reservationService;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public PaymentServiceImpl(
            ReservationRepository reservationRepository,
            PaymentRepository paymentRepository,
            ExtensionRequestRepository extensionRequestRepository,
            @Lazy ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.extensionRequestRepository = extensionRequestRepository;
        this.reservationService = reservationService;
    }

    @Override
    @Transactional
    public String startPaymentReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        if (!ReservationStatus.PENDING_PAYMENT.equals(reservation.getStatus())) {
            throw new InvalidPaymentStateException("Reservation is not pending payment");
        }

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/payment/success?reservationId=" + reservationId + "&session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/payment/cancel?reservationId=" + reservationId)
                    .putMetadata("paymentType", PaymentType.RESERVATION.name())
                    .putMetadata("reservationId", reservationId.toString())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(toStripeCents(reservation.getTotalAmount()))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Property Rental - Reservation #" + reservation.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            Payment payment = Payment.builder()
                    .reservation(reservation)
                    .amount(reservation.getTotalAmount())
                    .status("PENDING")
                    .paymentMethod("STRIPE")
                    .paymentType(PaymentType.RESERVATION)
                    .transactionId(session.getId())
                    .build();

            paymentRepository.save(payment);
            log.info("Stripe payment session created for Reservation ID: {}", reservationId);
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Error creating Stripe session", e);
            throw new RuntimeException("Failed to initialize payment gateway");
        }
    }

    @Override
    @Transactional
    public String startExtensionPayment(Long extensionRequestId) {
        ExtensionRequest request = extensionRequestRepository.findByIdAndStatus(extensionRequestId, ExtensionRequestStatus.APPROVED)
                .orElseThrow(() -> new InvalidPaymentStateException("Extension request not found or not approved"));

        Reservation reservation = request.getReservation();
        Long reservationId = reservation.getId();

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/payment/success?reservationId=" + reservationId + "&extensionRequestId=" + extensionRequestId + "&session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/payment/cancel?reservationId=" + reservationId)
                    .putMetadata("paymentType", PaymentType.EXTENSION.name())
                    .putMetadata("reservationId", reservationId.toString())
                    .putMetadata("extensionRequestId", extensionRequestId.toString())
                    .putMetadata("extraDays", request.getExtraDays().toString())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(toStripeCents(request.getQuotedAmount()))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Stay Extension - Reservation #" + reservationId)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            Payment payment = Payment.builder()
                    .reservation(reservation)
                    .amount(request.getQuotedAmount())
                    .status("PENDING")
                    .paymentMethod("STRIPE")
                    .paymentType(PaymentType.EXTENSION)
                    .extraDays(request.getExtraDays())
                    .extensionRequestId(extensionRequestId)
                    .transactionId(session.getId())
                    .build();

            paymentRepository.save(payment);
            log.info("Stripe extension session created for ExtensionRequest ID: {}", extensionRequestId);
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Error creating Stripe extension session", e);
            throw new RuntimeException("Failed to initialize extension payment gateway");
        }
    }

    @Override
    @Transactional
    public void handleCheckoutCancelled(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        if (!ReservationStatus.PENDING_PAYMENT.equals(reservation.getStatus())) {
            log.info("Checkout cancel ignored for reservation {} with status {}", reservationId, reservation.getStatus());
            return;
        }

        paymentRepository.findFirstByReservationIdAndStatusOrderByCreatedAtDesc(reservationId, "PENDING")
                .ifPresent(payment -> {
                    payment.setStatus("CANCELLED");
                    paymentRepository.save(payment);
                });

        reservationService.cancelOrReleaseExpiredBooking(reservationId);
        log.info("Checkout cancelled and calendar released for reservation {}", reservationId);
    }

    @Override
    @Transactional
    public void confirmPayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                completePaymentForSession(session);
            }
            return;
        }

        if ("checkout.session.expired".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                handleExpiredSession(session);
            }
        }
    }

    @Override
    @Transactional
    public String confirmPaymentBySessionId(String sessionId) {
        Payment payment = paymentRepository.findByTransactionId(sessionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for session ID: " + sessionId));

        if ("COMPLETED".equals(payment.getStatus())) {
            return payment.getStatus();
        }

        try {
            Session session = Session.retrieve(sessionId);
            if (!"paid".equals(session.getPaymentStatus())) {
                return payment.getStatus();
            }

            completePaymentForSession(session);
            return "COMPLETED";
        } catch (StripeException e) {
            log.error("Error retrieving Stripe session {}", sessionId, e);
            throw new RuntimeException("Failed to verify payment session");
        }
    }

    @Override
    @Transactional
    public void processPartialRefund(Long reservationId, BigDecimal refundAmount) {
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        List<Payment> payments = paymentRepository.findByReservationIdAndStatusInOrderByCreatedAtAsc(reservationId, REFUNDABLE_STATUSES);
        if (payments.isEmpty()) {
            throw new PaymentNotFoundException("No completed payment found for reservation ID: " + reservationId);
        }

        BigDecimal remaining = refundAmount.min(calculateRefundableBalance(payments));

        for (Payment payment : payments) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal alreadyRefunded = payment.getRefundedAmount() != null ? payment.getRefundedAmount() : BigDecimal.ZERO;
            BigDecimal available = payment.getAmount().subtract(alreadyRefunded);
            if (available.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal toRefund = remaining.min(available);
            executeStripeRefund(payment, toRefund);

            BigDecimal newRefundedTotal = alreadyRefunded.add(toRefund);
            payment.setRefundedAmount(newRefundedTotal);
            payment.setStatus(newRefundedTotal.compareTo(payment.getAmount()) >= 0 ? "REFUNDED" : "PARTIALLY_REFUNDED");
            paymentRepository.save(payment);

            remaining = remaining.subtract(toRefund);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new RefundProcessingException("Could not refund full amount. Remaining: " + remaining);
        }

        log.info("Partial refund of {} processed for reservation {}", refundAmount, reservationId);
    }

    @Override
    @Transactional
    public void refundSecurityDeposit(Long reservationId, BigDecimal amount) {
        List<Payment> payments = paymentRepository.findByReservationIdAndStatusInOrderByCreatedAtAsc(reservationId, REFUNDABLE_STATUSES);
        if (payments.isEmpty()) {
            throw new PaymentNotFoundException("No completed payment found for reservation ID: " + reservationId);
        }

        BigDecimal refundableBalance = calculateRefundableBalance(payments);
        BigDecimal refundAmount = amount != null ? amount : refundableBalance;

        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentStateException("No refundable balance available");
        }

        if (refundAmount.compareTo(refundableBalance) > 0) {
            refundAmount = refundableBalance;
        }

        processPartialRefund(reservationId, refundAmount);
    }

    @Override
    public String getPaymentStatus(Long reservationId) {
        List<Payment> payments = paymentRepository.findByReservationIdOrderByCreatedAtDesc(reservationId);

        if (payments.isEmpty()) {
            return "NO_PAYMENT_FOUND";
        }
        return payments.get(0).getStatus();
    }

    private void completePaymentForSession(Session session) {
        Payment payment = paymentRepository.findByTransactionId(session.getId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment record not found for Session ID: " + session.getId()));

        if ("COMPLETED".equals(payment.getStatus())) {
            return;
        }

        payment.setStatus("COMPLETED");
        payment.setPaidAt(LocalDateTime.now());

        if (session.getPaymentIntent() != null) {
            payment.setTransactionId(session.getPaymentIntent());
        }

        paymentRepository.save(payment);

        PaymentType paymentType = resolvePaymentType(session, payment);

        if (PaymentType.EXTENSION.equals(paymentType)) {
            completeExtensionPayment(payment, session);
        } else {
            completeReservationPayment(payment);
        }
    }

    private void completeReservationPayment(Payment payment) {
        Reservation reservation = payment.getReservation();
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
        log.info("Payment successfully confirmed for Reservation ID: {}", reservation.getId());
    }

    private void completeExtensionPayment(Payment payment, Session session) {
        Map<String, String> metadata = session.getMetadata();
        Long extensionRequestId = payment.getExtensionRequestId();

        if (extensionRequestId == null && metadata != null && metadata.get("extensionRequestId") != null) {
            extensionRequestId = Long.parseLong(metadata.get("extensionRequestId"));
        }

        if (extensionRequestId == null) {
            throw new InvalidPaymentStateException("Extension request ID missing from payment");
        }

        BookingExtensionResponseDTO result = reservationService.applyApprovedExtension(extensionRequestId, payment.getAmount());
        log.info("Extension payment confirmed for reservation {}: new checkout {}",
                result.getReservationId(), result.getNewCheckOutDate());
    }

    private void handleExpiredSession(Session session) {
        paymentRepository.findByTransactionId(session.getId()).ifPresent(payment -> {
            if ("PENDING".equals(payment.getStatus())) {
                payment.setStatus("CANCELLED");
                paymentRepository.save(payment);
            }

            if (PaymentType.RESERVATION.equals(payment.getPaymentType())
                    && ReservationStatus.PENDING_PAYMENT.equals(payment.getReservation().getStatus())) {
                reservationService.cancelOrReleaseExpiredBooking(payment.getReservation().getId());
            }
        });
    }

    private PaymentType resolvePaymentType(Session session, Payment payment) {
        if (payment.getPaymentType() != null) {
            return payment.getPaymentType();
        }
        Map<String, String> metadata = session.getMetadata();
        if (metadata != null && metadata.get("paymentType") != null) {
            return PaymentType.valueOf(metadata.get("paymentType"));
        }
        return PaymentType.RESERVATION;
    }

    private void executeStripeRefund(Payment payment, BigDecimal amount) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getTransactionId())
                    .setAmount(toStripeCents(amount))
                    .build();

            Refund.create(params);
        } catch (StripeException e) {
            log.error("Stripe refund failed for payment {}", payment.getId(), e);
            throw new RefundProcessingException("Error processing refund with payment gateway", e);
        }
    }

    private BigDecimal calculateRefundableBalance(List<Payment> payments) {
        return payments.stream()
                .map(payment -> {
                    BigDecimal refunded = payment.getRefundedAmount() != null ? payment.getRefundedAmount() : BigDecimal.ZERO;
                    return payment.getAmount().subtract(refunded);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long toStripeCents(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValue();
    }
}
