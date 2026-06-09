package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExtensionQuoteResponseDTO {
    Long id;
    int extraDays;
    BigDecimal pricePerNight;
    BigDecimal extensionSubtotal;

}
