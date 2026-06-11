package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@Builder
public class CancellationQuoteResponseDTO {
    private Long reservationId;
    private BigDecimal originalPricePaid;
    private BigDecimal penaltyFee;
    private BigDecimal refundAmount;

}
