package com.proyecto.proyectoncapas.controller.payments;

import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.PaymentInitResponseDTO;
import com.proyecto.proyectoncapas.dto.response.PaymentStatusResponseDTO;
import com.proyecto.proyectoncapas.services.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Stripe checkout sessions, payment confirmation, and refunds")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout/{reservationId}")
    @Operation(summary = "Start reservation checkout", description = "Create a Stripe checkout session URL for a pending reservation payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout URL returned"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "409", description = "Reservation is not in PENDING_PAYMENT state")
    })
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
    @Operation(summary = "Start extension checkout", description = "Create a Stripe checkout session URL for an approved stay extension payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extension checkout URL returned"),
            @ApiResponse(responseCode = "409", description = "Extension request not found or not approved")
    })
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
    @Operation(summary = "Cancel checkout", description = "Cancel an in-progress checkout and release the reservation's calendar slots")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout cancelled and reservation released")
    })
    public ResponseEntity<GeneralResponse<Void>> cancelCheckout(@PathVariable Long reservationId) {
        paymentService.handleCheckoutCancelled(reservationId);

        return ResponseEntity.ok(
                GeneralResponse.<Void>builder()
                        .message("Checkout cancelled and reservation released successfully")
                        .build()
        );
    }

    @PostMapping("/refund/{reservationId}")
    @Operation(summary = "Refund deposit", description = "Process a partial or full refund for a completed reservation. Omit amount to refund the full available balance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refund processed"),
            @ApiResponse(responseCode = "404", description = "No completed payment found"),
            @ApiResponse(responseCode = "409", description = "No refundable balance available")
    })
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
    @Operation(summary = "Confirm payment by session ID", description = "Manually confirm a payment using the Stripe session ID (fallback if webhook was not received)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment status returned"),
            @ApiResponse(responseCode = "404", description = "Payment not found for this session")
    })
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
    @Operation(summary = "Get payment status", description = "Get the current payment status for a reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status returned")
    })
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
