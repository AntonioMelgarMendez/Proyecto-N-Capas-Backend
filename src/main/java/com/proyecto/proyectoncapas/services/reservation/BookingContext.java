package com.proyecto.proyectoncapas.services.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class BookingContext {
    private final int totalDays;
    private final BigDecimal basePricePerNight;
    private final int numberOfGuests;


    // Nuevos campos para la lógica de cancelación
    private final LocalDate checkInDate;
    private final LocalDate cancellationDate;

    // Nuevos campos para extensiones de estadía
    private final boolean isExtension;
    private final int extendedDays;

    public boolean isCancellation() {
        return cancellationDate != null;
    }
}