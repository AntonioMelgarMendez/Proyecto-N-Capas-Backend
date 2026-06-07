package com.proyecto.proyectoncapas.services.payment.impl;

import com.proyecto.proyectoncapas.entities.Payment;
import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.repository.PaymentRepository;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.services.payment.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    @Transactional
    public String startPaymentReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/payment/success?reservationId=" + reservationId)
                    .setCancelUrl(frontendUrl + "/payment/cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd") // Assuming USD
                                                    // Stripe requires amounts in cents
                                                    .setUnitAmount(reservation.getTotalAmount().multiply(new BigDecimal(100)).longValue())
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
    public void confirmPayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null) {
                Payment payment = paymentRepository.findByTransactionId(session.getId())
                        .orElseThrow(() -> new RuntimeException("Payment record not found for Session ID: " + session.getId()));

                payment.setStatus("COMPLETED");
                payment.setPaidAt(LocalDateTime.now());

                if(session.getPaymentIntent() != null) {
                    payment.setTransactionId(session.getPaymentIntent());
                }

                paymentRepository.save(payment);
                Reservation reservation = payment.getReservation();
                reservation.setStatus("CONFIRMED");
                reservationRepository.save(reservation);

                log.info("Payment successfully confirmed for Reservation ID: {}", reservation.getId());
            }
        }
    }
    @Override
    @Transactional
    public void refundSecurityDeposit(Long reservationId) {
        Payment payment = paymentRepository.findFirstByReservationIdAndStatusOrderByCreatedAtDesc(reservationId, "COMPLETED")
                .orElseThrow(() -> new RuntimeException("No se encontró un pago completado para la reserva ID: " + reservationId));

        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getTransactionId())
                    .build();

            Refund refund = Refund.create(params);
            payment.setStatus("REFUNDED");
            paymentRepository.save(payment);

            log.info("Depósito reembolsado exitosamente para la reserva: {} - Payment ID: {}", reservationId, payment.getId());

        } catch (StripeException e) {
            log.error("Error al procesar el reembolso en Stripe para la reserva: {}", reservationId, e);
            throw new RuntimeException("Error al procesar el reembolso con la pasarela de pagos");
        }
    }

    @Override
    public String getPaymentStatus(Long reservationId) {
        List<Payment> payments = paymentRepository.findByReservationIdOrderByCreatedAtDesc(reservationId);

        if (payments.isEmpty()) {
            return "NO_PAYMENT_FOUND";
        }
        return payments.get(0).getStatus();
    }
}