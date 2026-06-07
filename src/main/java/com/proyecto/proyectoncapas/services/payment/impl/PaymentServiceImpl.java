package com.proyecto.proyectoncapas.services.payment.impl;

import com.proyecto.proyectoncapas.entities.Reservation;
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

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ReservationRepository reservationRepository;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    @Transactional
    public String startPaymentReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

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

            // Save the session ID and update status
            reservation.setStripeSessionId(session.getId());
            reservation.setPaymentStatus("PENDING");
            reservationRepository.save(reservation);

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
                Reservation reservation = reservationRepository.findByStripeSessionId(session.getId())
                        .orElseThrow(() -> new RuntimeException("Reservation not found for Session ID: " + session.getId()));

                reservation.setPaymentStatus("CONFIRMED");
                reservationRepository.save(reservation);

                log.info("Payment successfully confirmed for Reservation ID: {}", reservation.getId());

                // Note: The rest of the team can later add logic here to trigger emails,
                // generate digital contracts, or update property availability.
            }
        } else {
            log.warn("Unhandled Stripe event type: {}", event.getType());
        }
    }
    @Override
    @Transactional
    public void refundSecurityDeposit(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!"CONFIRMED".equals(reservation.getPaymentStatus())) {
            throw new RuntimeException("No se puede reembolsar un pago que no está confirmado");
        }

        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(reservation.getStripePaymentIntentId())
                    .build();

            Refund refund = Refund.create(params);
            reservation.setPaymentStatus("REFUNDED");
            reservationRepository.save(reservation);

            log.info("Depósito reembolsado para la reserva: {}", reservationId);

        } catch (StripeException e) {
            log.error("Error al procesar el reembolso en Stripe", e);
            throw new RuntimeException("Error al procesar el reembolso con la pasarela de pagos");
        }
    }

    @Override
    public String getPaymentStatus(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        return reservation.getPaymentStatus();
    }
}