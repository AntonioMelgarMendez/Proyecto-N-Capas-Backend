package com.proyecto.proyectoncapas.controllers;

import com.proyecto.proyectoncapas.services.payment.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/stripe")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader) {

        try {
            // Verify the event came from Stripe
            Event event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);

            // Process the confirmation
            paymentService.confirmPayment(event);

            // Return 200 OK so Stripe knows we received it
            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature", e);
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        } catch (Exception e) {
            log.error("Error processing Stripe webhook", e);
            return ResponseEntity.internalServerError().build(); // 500 Internal Server Error
        }
    }
}