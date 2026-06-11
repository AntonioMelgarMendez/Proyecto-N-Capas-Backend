package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.services.reservation.PriceCalculator;
import com.proyecto.proyectoncapas.services.reservation.RuleComponentResolver;
import com.proyecto.proyectoncapas.utils.enums.RuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Component
public class CancellationPenaltyResolver implements RuleComponentResolver {

    @Override
    public RuleType getSupportedType() {
        return RuleType.CANCELLATION_PENALTY;
    }

    @Override
    public PriceCalculator decorate(PriceCalculator currentCalculator, BigDecimal basePenaltyValue) {
        return context -> {
            BigDecimal currentPrice = currentCalculator.calculate(context);

            // Si no es un evento de cancelación, ignoramos esta regla y pasamos el precio actual
            if (!context.isCancellation()) {
                return currentPrice;
            }

            // Calcular cuántos días antes del Check-In se está cancelando
            long daysNotice = ChronoUnit.DAYS.between(context.getCancellationDate(), context.getCheckInDate());

            BigDecimal penaltyToApply = BigDecimal.ZERO;

            if (daysNotice < 0) {
                // Cancelación tardía/No-show (ya pasó la fecha de check-in): Penalización total
                penaltyToApply = basePenaltyValue;
            } else if (daysNotice < 7) {
                // Menos de una semana de anticipación: 100% de la penalización base
                penaltyToApply = basePenaltyValue;
            } else if (daysNotice >= 7 && daysNotice <= 14) {
                // Entre 7 y 14 días: Cobrar el 50% de la penalización base de forma proporcional
                penaltyToApply = basePenaltyValue.multiply(new BigDecimal("0.50"));
            } // Si es más de 14 días, penaltyToApply se queda en ZERO

            //  sumamos al total a pagar por el trámite.
            return currentPrice.add(penaltyToApply);
        };
    }
}