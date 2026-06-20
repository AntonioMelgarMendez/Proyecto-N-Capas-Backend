package com.proyecto.proyectoncapas.services.analytics.impl;

import com.proyecto.proyectoncapas.dto.response.OccupancyMetricsResponseDTO;
import com.proyecto.proyectoncapas.dto.response.MaintenanceMetricsResponseDTO;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.repository.MaintenanceTicketRepository;
import com.proyecto.proyectoncapas.services.analytics.AnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Object[]> results = reservationRepository.getOccupancyMetricsByLandlord(
                landlordId,
                startDateTime,
                endDateTime
        );

        long rangeDays = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));

        return results.stream()
                .map(row -> {
                    Long propertyId = ((Number) row[0]).longValue();
                    String propertyTitle = (String) row[1];
                    Integer totalReservations = ((Number) row[2]).intValue();
                    BigDecimal totalRevenue = new BigDecimal(row[3].toString());
                    Integer totalDaysOccupied = ((Number) row[4]).intValue();

                    Double occupancyPercentage = totalDaysOccupied > 0
                            ? (totalDaysOccupied * 100.0) / rangeDays
                            : 0.0;

                    return OccupancyMetricsResponseDTO.builder()
                            .propertyId(propertyId)
                            .propertyTitle(propertyTitle)
                            .totalReservations(totalReservations)
                            .totalRevenue(totalRevenue)
                            .occupancyPercentage(occupancyPercentage)
                            .totalDaysOccupied(totalDaysOccupied)
                            .build();
                })
                .toList();
    }

    @Override
    public List<MaintenanceMetricsResponseDTO> getMaintenanceMetrics(Long landlordId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = ticketRepository.getMaintenanceMetricsByProperty(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59),
                landlordId
        );

        return results.stream()
                .map(row -> MaintenanceMetricsResponseDTO.builder()
                        .propertyId(((Number) row[0]).longValue())
                        .propertyTitle((String) row[1])
                        .totalTickets(((Number) row[2]).longValue())
                        .openTickets(((Number) row[3]).longValue())
                        .inProgressTickets(((Number) row[4]).longValue())
                        .resolvedTickets(((Number) row[5]).longValue())
                        .closedTickets(((Number) row[6]).longValue())
                        .resolutionRate(calculateResolutionRate(row))
                        .build())
                .toList();
    }

    @Override
    public OccupancyMetricsResponseDTO getPropertyMetrics(Long propertyId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Object[]> results = reservationRepository.getOccupancyMetricsByProperty(
                propertyId,
                startDateTime,
                endDateTime
        );

        if (results.isEmpty()) {
            return OccupancyMetricsResponseDTO.builder()
                    .propertyId(propertyId)
                    .propertyTitle("N/A")
                    .totalReservations(0)
                    .totalRevenue(BigDecimal.ZERO)
                    .occupancyPercentage(0.0)
                    .totalDaysOccupied(0)
                    .build();
        }

        Object[] row = results.getFirst();
        Long pId = ((Number) row[0]).longValue();
        String pTitle = (String) row[1];
        Integer totalRes = ((Number) row[2]).intValue();
        BigDecimal revenue = new BigDecimal(row[3].toString());
        int daysOccupied = ((Number) row[4]).intValue();

        long rangeDays = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));
        Double occupancyPercentage = daysOccupied > 0
                ? (daysOccupied * 100.0) / rangeDays
                : 0.0;

        return OccupancyMetricsResponseDTO.builder()
                .propertyId(pId)
                .propertyTitle(pTitle)
                .totalReservations(totalRes)
                .totalRevenue(revenue)
                .occupancyPercentage(occupancyPercentage)
                .totalDaysOccupied(daysOccupied)
                .build();
    }

    private Double calculateResolutionRate(Object[] row) {
        long total = ((Number) row[2]).longValue();
        long resolved = ((Number) row[5]).longValue();
        return total > 0 ? (resolved * 100.0) / total : 0.0;
    }
}