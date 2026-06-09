package com.proyecto.proyectoncapas.services.reservation;

import java.math.BigDecimal;

@FunctionalInterface
public interface PriceCalculator {
    BigDecimal calculate(BookingContext context);
}