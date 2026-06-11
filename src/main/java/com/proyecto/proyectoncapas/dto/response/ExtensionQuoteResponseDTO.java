package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExtensionQuoteResponseDTO {
    private Long id;
    private int extraDays;
    private BigDecimal pricePerNight;
    private BigDecimal baseAmount;
    private BigDecimal extensionFeePerNight;
    private BigDecimal extensionFeeTotal;
    private BigDecimal discountAmount;
    private BigDecimal surchargeAmount;
    private BigDecimal extensionSubtotal;
}
