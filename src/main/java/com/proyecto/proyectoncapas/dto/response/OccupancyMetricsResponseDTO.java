package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyMetricsResponseDTO {
    private Long propertyId;
    private String propertyTitle;
    private Integer totalReservations;
    private BigDecimal totalRevenue;
    private Double occupancyPercentage;
    private Integer totalDaysOccupied;
}
