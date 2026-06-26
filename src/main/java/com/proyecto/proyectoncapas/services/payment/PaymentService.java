package com.proyecto.proyectoncapas.services.payment;

import com.stripe.model.Event;

import java.math.BigDecimal;

public interface PaymentService {
    String startPaymentReservation(Long reservationId);
    String startExtensionPayment(Long extensionRequestId);
    void handleCheckoutCancelled(Long reservationId);
    void confirmPayment(Event event);
    String confirmPaymentBySessionId(String sessionId);
    void processPartialRefund(Long reservationId, BigDecimal refundAmount);
    void refundSecurityDeposit(Long reservationId, BigDecimal amount);
    String getPaymentStatus(Long reservationId);
}
