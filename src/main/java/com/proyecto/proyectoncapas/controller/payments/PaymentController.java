package com.proyecto.proyectoncapas.controller.payments;

import com.proyecto.proyectoncapas.dto.response.PaymentInitResponseDTO;
import com.proyecto.proyectoncapas.services.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Hacer pago
    @PostMapping("/checkout/{reservationId}")
    public ResponseEntity<PaymentInitResponseDTO> createCheckoutSession(@PathVariable Long reservationId) {
        String checkoutUrl = paymentService.startPaymentReservation(reservationId);
        return ResponseEntity.ok(new PaymentInitResponseDTO(checkoutUrl));
    }

    // Reembolsar el depósito de garantía
    @PostMapping("/refund/{reservationId}")
    public ResponseEntity<Map<String, String>> refundSecurityDeposit(@PathVariable Long reservationId) {
        // Llama al servicio para procesar el reembolso en Stripe
        paymentService.refundSecurityDeposit(reservationId);
        return ResponseEntity.ok(Map.of("message", "Reembolso procesado exitosamente"));
    }

    // Consultar el estado del pago
    @GetMapping("/status/{reservationId}")
    public ResponseEntity<Map<String, String>> getPaymentStatus(@PathVariable Long reservationId) {
        String status = paymentService.getPaymentStatus(reservationId);
        return ResponseEntity.ok(Map.of("paymentStatus", status));
    }
}