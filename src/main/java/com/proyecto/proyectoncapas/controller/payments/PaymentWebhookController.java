package com.proyecto.proyectoncapas.controller.payments;

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
            @RequestHeader("Stripe-Signature") String signatureHeader) throws SignatureVerificationException {

        //Verificamos la firma (Si falla, el GlobalExceptionHandler lanza el ApiError automáticamente)
        Event event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);

        //Procesamos el pago en el servicio
        paymentService.confirmPayment(event);

        //Retornamos un 200 OK vacío exclusivo para los servidores de Stripe
        return ResponseEntity.ok().build();
    }
}