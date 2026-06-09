package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class CancellationResponseDTO {
    private Long reservationId;
    private String status;
    private BigDecimal penaltyCharged;
    private BigDecimal amountRefunded;
}
