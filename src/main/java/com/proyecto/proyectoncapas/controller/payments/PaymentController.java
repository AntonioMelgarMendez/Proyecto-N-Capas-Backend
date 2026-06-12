package com.proyecto.proyectoncapas.controller.payments;

import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.PaymentInitResponseDTO;
import com.proyecto.proyectoncapas.dto.response.PaymentStatusResponseDTO;
import com.proyecto.proyectoncapas.services.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout/{reservationId}")
    public ResponseEntity<GeneralResponse<PaymentInitResponseDTO>> createCheckoutSession(@PathVariable Long reservationId) {
        String checkoutUrl = paymentService.startPaymentReservation(reservationId);

        return ResponseEntity.ok(
                GeneralResponse.<PaymentInitResponseDTO>builder()
                        .message("Checkout session created successfully")
                        .data(new PaymentInitResponseDTO(checkoutUrl))
                        .build()
        );
    }

    @PostMapping("/checkout/extension/{extensionRequestId}")
    public ResponseEntity<GeneralResponse<PaymentInitResponseDTO>> createExtensionCheckoutSession(
            @PathVariable Long extensionRequestId) {
        String checkoutUrl = paymentService.startExtensionPayment(extensionRequestId);

        return ResponseEntity.ok(
                GeneralResponse.<PaymentInitResponseDTO>builder()
                        .message("Extension checkout session created successfully")
                        .data(new PaymentInitResponseDTO(checkoutUrl))
                        .build()
        );
    }

    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<GeneralResponse<Void>> cancelCheckout(@PathVariable Long reservationId) {
        paymentService.handleCheckoutCancelled(reservationId);

        return ResponseEntity.ok(
                GeneralResponse.<Void>builder()
                        .message("Checkout cancelled and reservation released successfully")
                        .build()
        );
    }

    @PostMapping("/refund/{reservationId}")
    public ResponseEntity<GeneralResponse<String>> refundSecurityDeposit(
            @PathVariable Long reservationId,
            @RequestParam(required = false) BigDecimal amount) {
        paymentService.refundSecurityDeposit(reservationId, amount);

        return ResponseEntity.ok(
                GeneralResponse.<String>builder()
                        .message("Refund processed successfully")
                        .data("Refund has been processed")
                        .build()
        );
    }

    @PostMapping("/confirm-session/{sessionId}")
    public ResponseEntity<GeneralResponse<PaymentStatusResponseDTO>> confirmCheckoutSession(@PathVariable String sessionId) {
        String status = paymentService.confirmPaymentBySessionId(sessionId);

        return ResponseEntity.ok(
                GeneralResponse.<PaymentStatusResponseDTO>builder()
                        .message("Payment session verified successfully")
                        .data(new PaymentStatusResponseDTO(status))
                        .build()
        );
    }

    @GetMapping("/status/{reservationId}")
    public ResponseEntity<GeneralResponse<PaymentStatusResponseDTO>> getPaymentStatus(@PathVariable Long reservationId) {
        String status = paymentService.getPaymentStatus(reservationId);

        return ResponseEntity.ok(
                GeneralResponse.<PaymentStatusResponseDTO>builder()
                        .message("Payment status retrieved successfully")
                        .data(new PaymentStatusResponseDTO(status))
                        .build()
        );
    }
}
