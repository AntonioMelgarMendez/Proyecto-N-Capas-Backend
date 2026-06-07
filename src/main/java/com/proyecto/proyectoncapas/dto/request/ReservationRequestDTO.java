package com.proyecto.proyectoncapas.dto.request;

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
public class ReservationRequestDTO {

    private LocalDate checkIn;

    private LocalDate checkOut;

    private BigDecimal totalAmount;
    // Espacios listos para que el equipo agregue las relaciones cuando creen esas tablas
    // private Long propertyId;
    // private Long inquilinoId;
}