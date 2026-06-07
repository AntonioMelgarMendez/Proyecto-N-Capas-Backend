package com.proyecto.proyectoncapas.services.payment;
import com.stripe.model.Event;
public interface PaymentService {
    String startPaymentReservation(Long reservationId);
    void confirmPayment(Event event);
    void refundSecurityDeposit(Long reservationId);
    String getPaymentStatus(Long reservationId);
}
