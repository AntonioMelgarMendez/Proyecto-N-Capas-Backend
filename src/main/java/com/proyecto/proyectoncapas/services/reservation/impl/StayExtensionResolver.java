package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.services.reservation.PriceCalculator;
import com.proyecto.proyectoncapas.services.reservation.RuleComponentResolver;
import com.proyecto.proyectoncapas.utils.enums.RuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StayExtensionResolver implements RuleComponentResolver {

    @Override
    public RuleType getSupportedType() {
        return RuleType.STAY_EXTENSION_FEE;
    }

    @Override
    public PriceCalculator decorate(PriceCalculator currentCalculator, BigDecimal extensionFeePerNight) {
        return context -> {
            // Calculamos el precio acumulado hasta el momento (que ya incluye las noches base totales)
            BigDecimal currentPrice = currentCalculator.calculate(context);

            // Si no es un flujo de extensión de estadía, ignoramos la regla
            if (!context.isExtension() || context.getExtendedDays() <= 0) {
                return currentPrice;
            }

            // Calculamos el recargo: (Días extendidos * Tarifa de recargo por noche)
            BigDecimal totalExtensionPenalty = extensionFeePerNight.multiply(BigDecimal.valueOf(context.getExtendedDays()));

            // Sumamos el recargo por cambio de último minuto al total de la orden
            return currentPrice.add(totalExtensionPenalty);
        };
    }
}