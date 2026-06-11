package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.entities.Property;
import com.proyecto.proyectoncapas.entities.PropertyRule;
import com.proyecto.proyectoncapas.services.reservation.BookingContext;
import com.proyecto.proyectoncapas.services.reservation.PriceCalculator;
import com.proyecto.proyectoncapas.services.reservation.RuleComponentResolver;
import com.proyecto.proyectoncapas.utils.enums.RuleType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PriceCalculationService {

    private final Map<RuleType, RuleComponentResolver> resolvers;

    // Spring inyecta automáticamente todos los @Component que implementan RuleComponentResolver
    public PriceCalculationService(List<RuleComponentResolver> resolverList) {
        this.resolvers = resolverList.stream()
                .collect(Collectors.toMap(RuleComponentResolver::getSupportedType, resolver -> resolver));
    }

    // acepta la lista ya filtrada por el negocio
    public BigDecimal calculateFinalPriceWithCustomRules(List<PropertyRule> customRules, BookingContext context) {
        PriceCalculator pipeline = ctx -> ctx.getBasePricePerNight()
                .multiply(BigDecimal.valueOf(ctx.getTotalDays()));

        for (PropertyRule rule : customRules) {
            RuleComponentResolver resolver = resolvers.get(rule.getRuleType());
            if (resolver != null) {
                pipeline = resolver.decorate(pipeline, rule.getValue());
            }
        }

        return pipeline.calculate(context);
    }

    // Utiliza todas las reglas de la propiedad.
    public BigDecimal calculateFinalPrice(Property property, BookingContext context) {
        return calculateFinalPriceWithCustomRules(property.getRules(), context);
    }
}