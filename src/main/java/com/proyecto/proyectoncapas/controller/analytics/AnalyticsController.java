package com.proyecto.proyectoncapas.controller.analytics;

import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.MaintenanceMetricsResponseDTO;
import com.proyecto.proyectoncapas.dto.response.OccupancyMetricsResponseDTO;
import com.proyecto.proyectoncapas.services.analytics.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting endpoints for occupancy and maintenance metrics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/occupancy/landlord/{landlordId}")
    @Operation(summary = "Get occupancy metrics for landlord",
            description = "Retrieve occupancy and revenue metrics for all properties of a specific landlord within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Occupancy metrics retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format or parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<List<OccupancyMetricsResponseDTO>>> getOccupancyMetricsByLandlord(
            @PathVariable
            @Parameter(description = "Landlord ID", required = true)
            Long landlordId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Start date (YYYY-MM-DD)", required = true)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "End date (YYYY-MM-DD)", required = true)
            LocalDate endDate) {

        List<OccupancyMetricsResponseDTO> metrics = analyticsService.getOccupancyMetrics(
                landlordId,
                startDate,
                endDate
        );

        return ResponseEntity.ok(
                GeneralResponse.<List<OccupancyMetricsResponseDTO>>builder()
                        .message("Occupancy metrics retrieved successfully")
                        .data(metrics)
                        .build()
        );
    }

    @GetMapping("/occupancy/property/{propertyId}")
    @Operation(summary = "Get occupancy metrics for a specific property",
            description = "Retrieve occupancy and revenue metrics for a specific property within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property metrics retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format or parameters"),
            @ApiResponse(responseCode = "404", description = "Property not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<OccupancyMetricsResponseDTO>> getOccupancyMetricsByProperty(
            @PathVariable
            @Parameter(description = "Property ID", required = true)
            Long propertyId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Start date (YYYY-MM-DD)", required = true)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "End date (YYYY-MM-DD)", required = true)
            LocalDate endDate) {

        OccupancyMetricsResponseDTO metrics = analyticsService.getPropertyMetrics(
                propertyId,
                startDate,
                endDate
        );

        return ResponseEntity.ok(
                GeneralResponse.<OccupancyMetricsResponseDTO>builder()
                        .message("Property metrics retrieved successfully")
                        .data(metrics)
                        .build()
        );
    }

    @GetMapping("/maintenance/landlord/{landlordId}")
    @Operation(summary = "Get maintenance metrics for landlord",
            description = "Retrieve maintenance ticket analytics including total, open, in-progress, and resolved tickets with resolution rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance metrics retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format or parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<List<MaintenanceMetricsResponseDTO>>> getMaintenanceMetrics(
            @PathVariable
            @Parameter(description = "Landlord ID", required = true)
            Long landlordId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Start date (YYYY-MM-DD)", required = true)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "End date (YYYY-MM-DD)", required = true)
            LocalDate endDate) {

        List<MaintenanceMetricsResponseDTO> metrics = analyticsService.getMaintenanceMetrics(
                landlordId,
                startDate,
                endDate
        );

        return ResponseEntity.ok(
                GeneralResponse.<List<MaintenanceMetricsResponseDTO>>builder()
                        .message("Maintenance metrics retrieved successfully")
                        .data(metrics)
                        .build()
        );
    }
}

