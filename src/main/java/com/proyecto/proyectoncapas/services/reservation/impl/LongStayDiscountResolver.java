package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.services.reservation.PriceCalculator;
import com.proyecto.proyectoncapas.services.reservation.RuleComponentResolver;
import com.proyecto.proyectoncapas.utils.enums.RuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LongStayDiscountResolver implements RuleComponentResolver  {
    @Override
    public RuleType getSupportedType() { 
        return RuleType.LONG_STAY_DISCOUNT;
    }

    @Override
    public PriceCalculator decorate(PriceCalculator currentCalculator, BigDecimal discountPercentage) {
        return context -> {
            BigDecimal currentPrice = currentCalculator.calculate(context);
            // Si la estadía es mayor a 4 días, aplica el porcentaje de descuento
            if (context.getTotalDays() > 4) {
                BigDecimal discount = currentPrice.multiply(discountPercentage);
                return currentPrice.subtract(discount);
            }
            return currentPrice;
        };
    }
}