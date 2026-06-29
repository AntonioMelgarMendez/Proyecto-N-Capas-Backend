package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionRequestResponseDTO {
    private Long id;
    private Long reservationId;
    private Integer extraDays;
    private BigDecimal quotedAmount;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
    private Long resolvedById;
}
