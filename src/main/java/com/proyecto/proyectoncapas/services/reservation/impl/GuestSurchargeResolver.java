package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.services.reservation.PriceCalculator;
import com.proyecto.proyectoncapas.services.reservation.RuleComponentResolver;
import com.proyecto.proyectoncapas.utils.enums.RuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GuestSurchargeResolver implements RuleComponentResolver {

    @Override
    public RuleType getSupportedType() {
        return RuleType.GUEST_SURCHARGE;
    }

    @Override
    public PriceCalculator decorate(PriceCalculator currentCalculator, BigDecimal feePerGuestPerNight) {
        return context -> {
            // Evaluamos el precio acumulado hasta el momento
            BigDecimal currentPrice = currentCalculator.calculate(context);

            // Si solo hay 1 huésped, asumimos que está cubierto por la tarifa base de la propiedad
            if (context.getNumberOfGuests() <= 1) {
                return currentPrice;
            }

            // Calculamos cuántos huéspedes extra hay
            int extraGuests = context.getNumberOfGuests() - 1;

            // Tarifa total extra = (Huéspedes Extra * Costo por Persona) * Total de Noches
            BigDecimal totalSurcharge = feePerGuestPerNight
                    .multiply(BigDecimal.valueOf(extraGuests))
                    .multiply(BigDecimal.valueOf(context.getTotalDays()));

            return currentPrice.add(totalSurcharge);
        };
    }
}