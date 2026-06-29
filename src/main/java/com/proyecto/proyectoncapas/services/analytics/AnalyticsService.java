package com.proyecto.proyectoncapas.services.analytics;

import com.proyecto.proyectoncapas.dto.response.OccupancyMetricsResponseDTO;
import com.proyecto.proyectoncapas.dto.response.MaintenanceMetricsResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {
    List<OccupancyMetricsResponseDTO> getOccupancyMetrics(Long landlordId, LocalDate startDate, LocalDate endDate);
    List<MaintenanceMetricsResponseDTO> getMaintenanceMetrics(Long landlordId, LocalDate startDate, LocalDate endDate);
    OccupancyMetricsResponseDTO getPropertyMetrics(Long propertyId, LocalDate startDate, LocalDate endDate);
}