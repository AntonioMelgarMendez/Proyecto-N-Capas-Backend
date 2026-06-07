package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {

    private Long id;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private BigDecimal totalAmount;

    private String paymentStatus;

    private String stripeSessionId;


    // Espacios para el futuro
    // private String propertyName;
    // private String inquilinoName;
}