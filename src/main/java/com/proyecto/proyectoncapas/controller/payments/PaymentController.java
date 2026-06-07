package com.proyecto.proyectoncapas.controller.payments;

import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.PaymentInitResponseDTO;
import com.proyecto.proyectoncapas.dto.response.PaymentStatusResponseDTO;
import com.proyecto.proyectoncapas.services.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout/{reservationId}")
    public ResponseEntity<GeneralResponse<PaymentInitResponseDTO>> createCheckoutSession(@PathVariable Long reservationId) {
        String checkoutUrl = paymentService.startPaymentReservation(reservationId);

        GeneralResponse<PaymentInitResponseDTO> response = GeneralResponse.<PaymentInitResponseDTO>builder()
                .message("Checkout session created successfully")
                .data(new PaymentInitResponseDTO(checkoutUrl))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refund/{reservationId}")
    public ResponseEntity<GeneralResponse<String>> refundSecurityDeposit(@PathVariable Long reservationId) {
        paymentService.refundSecurityDeposit(reservationId);

        GeneralResponse<String> response = GeneralResponse.<String>builder()
                .message("Refund processed successfully")
                .data("Security deposit has been refunded")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{reservationId}")
    public ResponseEntity<GeneralResponse<PaymentStatusResponseDTO>> getPaymentStatus(@PathVariable Long reservationId) {
        String status = paymentService.getPaymentStatus(reservationId);

        GeneralResponse<PaymentStatusResponseDTO> response = GeneralResponse.<PaymentStatusResponseDTO>builder()
                .message("Payment status retrieved successfully")
                .data(new PaymentStatusResponseDTO(status))
                .build();

        return ResponseEntity.ok(response);
    }
}