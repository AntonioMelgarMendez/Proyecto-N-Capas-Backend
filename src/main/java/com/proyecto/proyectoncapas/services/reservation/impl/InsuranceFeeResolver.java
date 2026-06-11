package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.services.reservation.PriceCalculator;
import com.proyecto.proyectoncapas.services.reservation.RuleComponentResolver;
import com.proyecto.proyectoncapas.utils.enums.RuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InsuranceFeeResolver implements RuleComponentResolver {
    @Override
    public RuleType getSupportedType() {
        return RuleType.INSURANCE_FEE;
    }

    @Override
    public PriceCalculator decorate(PriceCalculator currentCalculator, BigDecimal insurancePerDay) {
        return context -> {
            BigDecimal currentPrice = currentCalculator.calculate(context);
            // Seguro por día multiplicando el valor por el total de días
            BigDecimal totalInsurance = insurancePerDay.multiply(BigDecimal.valueOf(context.getTotalDays()));
            return currentPrice.add(totalInsurance);
        };
    }
}