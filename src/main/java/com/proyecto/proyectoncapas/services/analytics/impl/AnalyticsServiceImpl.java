package com.proyecto.proyectoncapas.services.analytics.impl;

import com.proyecto.proyectoncapas.dto.response.OccupancyMetricsResponseDTO;
import com.proyecto.proyectoncapas.dto.response.MaintenanceMetricsResponseDTO;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.repository.MaintenanceTicketRepository;
import com.proyecto.proyectoncapas.services.analytics.AnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ReservationRepository reservationRepository;
    private final MaintenanceTicketRepository ticketRepository;

    @Override
    public List<OccupancyMetricsResponseDTO> getOccupancyMetrics(Long landlordId, LocalDate startDate, LocalDate endDate) {
        return null; // TODO
    }

    @Override
    public List<MaintenanceMetricsResponseDTO> getMaintenanceMetrics(Long landlordId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = ticketRepository.getMaintenanceMetricsByProperty(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        return results.stream()
                .map(row -> MaintenanceMetricsResponseDTO.builder()
                        .propertyId(((Number) row[0]).longValue())
                        .propertyTitle((String) row[1])
                        .totalTickets(((Number) row[2]).longValue())
                        .openTickets(((Number) row[3]).longValue())
                        .inProgressTickets(((Number) row[4]).longValue())
                        .resolvedTickets(((Number) row[5]).longValue())
                        .resolutionRate(calculateResolutionRate(row))
                        .build())
                .toList();
    }

    @Override
    public OccupancyMetricsResponseDTO getPropertyMetrics(Long propertyId, LocalDate startDate, LocalDate endDate) {
        // TODO
        return null;
    }

    private Double calculateResolutionRate(Object[] row) {
        long total = ((Number) row[2]).longValue();
        long resolved = ((Number) row[5]).longValue();
        return total > 0 ? (resolved * 100.0) / total : 0.0;
    }
}