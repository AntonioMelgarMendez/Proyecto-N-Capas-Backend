package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.services.reservation.PriceCalculator;
import com.proyecto.proyectoncapas.services.reservation.RuleComponentResolver;
import com.proyecto.proyectoncapas.utils.enums.RuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CleaningFeeResolver implements RuleComponentResolver {
    @Override
    public RuleType getSupportedType() {
        return RuleType.CLEANING_FEE; 
    }

    @Override
    public PriceCalculator decorate(PriceCalculator currentCalculator, BigDecimal fee) {
        // Retorna una función lambda que añade el costo fijo de limpieza al acumulado
        return context -> currentCalculator.calculate(context).add(fee);
    }
}