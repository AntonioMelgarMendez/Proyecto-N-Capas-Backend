package com.proyecto.proyectoncapas.controller.payments;

import com.proyecto.proyectoncapas.services.payment.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/stripe")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhooks", description = "Stripe webhook receiver — do not call manually")
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping
    @Operation(summary = "Handle Stripe webhook", description = "Receives and processes Stripe events (checkout.session.completed, checkout.session.expired). Called by Stripe only — requires valid signature header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event processed"),
            @ApiResponse(responseCode = "400", description = "Invalid Stripe signature")
    })
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader) throws SignatureVerificationException {

        //Verificamos la firma (Si falla, el GlobalExceptionHandler lanza el ApiError automáticamente)
        Event event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);

        //Procesamos el pago en el servicio
        paymentService.confirmPayment(event);

        //Retornamos un 200 OK vacío exclusivo para los servidores de Stripe
        return ResponseEntity.ok().build();
    }
}
