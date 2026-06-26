package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionRequestLandlordResponseDTO {
    private Long id;
    private Long reservationId;
    private Integer extraDays;
    private BigDecimal quotedAmount;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
    private Long resolvedById;
    private String propertyTitle;
    private String propertyCity;
    private String tenantName;
    private LocalDate currentCheckOutDate;
}
