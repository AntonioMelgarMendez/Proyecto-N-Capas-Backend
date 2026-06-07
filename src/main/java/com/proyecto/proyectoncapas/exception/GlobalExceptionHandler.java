package com.proyecto.proyectoncapas.exception;

import com.stripe.exception.SignatureVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja cualquier "Not Found" (Reservas, Contratos, Usuarios)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), null);
    }

    // Maneja los conflictos de negocio (ej. Contrato ya firmado)
    @ExceptionHandler(ContractAlreadySignedException.class)
    public ResponseEntity<ApiError> handleContractAlreadySigned(ContractAlreadySignedException ex) {
        return buildResponseEntity(HttpStatus.CONFLICT, "Business Conflict", ex.getMessage(), null);
    }

    // Maneja los errores de los @Valid (ej. email inválido, campos nulos)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildResponseEntity(HttpStatus.BAD_REQUEST, "Validation Failed", "Invalid input data", errors);
    }

    // Maneja cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllOtherExceptions(Exception ex) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred", null);
    }
    // Manejar firmas 
    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<ApiError> handleStripeSignatureException(SignatureVerificationException ex) {
        return buildResponseEntity(
                HttpStatus.BAD_REQUEST,
                "Invalid Webhook Signature",
                "The event payload signature could not be verified.",
                null
        );
    }

    // Error mas rapido
    private ResponseEntity<ApiError> buildResponseEntity(HttpStatus status, String error, String message, Object details) {
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .details(details)
                .build();
        return new ResponseEntity<>(apiError, status);
    }
}