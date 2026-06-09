package com.proyecto.proyectoncapas.services.reservation;

import com.proyecto.proyectoncapas.utils.enums.RuleType;

import java.math.BigDecimal;

public interface RuleComponentResolver {
    RuleType getSupportedType();
    PriceCalculator decorate(PriceCalculator currentCalculator, BigDecimal ruleValue);
}