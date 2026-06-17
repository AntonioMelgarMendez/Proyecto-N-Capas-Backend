package com.proyecto.proyectoncapas.controller.maintenance;

import com.proyecto.proyectoncapas.dto.request.MaintenanceTicketRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.MaintenanceTicketResponseDTO;
import com.proyecto.proyectoncapas.services.maintenance.MaintenanceTicketService;
import com.proyecto.proyectoncapas.utils.enums.TicketStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@Tag(name = "Maintenance Tickets", description = "Endpoints for managing maintenance tickets with photo support")
public class MaintenanceTicketController {

    private final MaintenanceTicketService maintenanceTicketService;

    @PostMapping("/tickets")
    @Operation(summary = "Create a new maintenance ticket",
            description = "Create a maintenance ticket with optional photo uploads for reporting damages or issues")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Maintenance ticket created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Property or tenant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<MaintenanceTicketResponseDTO>> createTicket(
            @RequestParam
            @Parameter(description = "Tenant ID", required = true)
            Long tenantId,

            @RequestPart
            @Parameter(description = "Maintenance ticket details", required = true)
            MaintenanceTicketRequestDTO request,

            @RequestPart(required = false)
            @Parameter(description = "Optional photos of the issue (multipart file upload)")
            List<MultipartFile> photos) {

        MaintenanceTicketResponseDTO ticket = maintenanceTicketService.createTicket(tenantId, request, photos);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GeneralResponse.<MaintenanceTicketResponseDTO>builder()
                        .message("Maintenance ticket created successfully")
                        .data(ticket)
                        .build());
    }

    @GetMapping("/tickets/{ticketId}")
    @Operation(summary = "Get maintenance ticket by ID",
            description = "Retrieve details of a specific maintenance ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<MaintenanceTicketResponseDTO>> getTicketById(
            @PathVariable
            @Parameter(description = "Ticket ID", required = true)
            Long ticketId) {

        MaintenanceTicketResponseDTO ticket = maintenanceTicketService.getTicketById(ticketId);

        return ResponseEntity.ok(
                GeneralResponse.<MaintenanceTicketResponseDTO>builder()
                        .message("Ticket retrieved successfully")
                        .data(ticket)
                        .build());
    }

    @GetMapping("/tickets/tenant/{tenantId}")
    @Operation(summary = "Get all tickets reported by a tenant",
            description = "Retrieve all maintenance tickets created by a specific tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tenant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<List<MaintenanceTicketResponseDTO>>> getTicketsByTenant(
            @PathVariable
            @Parameter(description = "Tenant ID", required = true)
            Long tenantId) {

        List<MaintenanceTicketResponseDTO> tickets = maintenanceTicketService.getTicketsByTenant(tenantId);

        return ResponseEntity.ok(
                GeneralResponse.<List<MaintenanceTicketResponseDTO>>builder()
                        .message("Tickets retrieved successfully")
                        .data(tickets)
                        .build());
    }

    @GetMapping("/tickets/property/{propertyId}")
    @Operation(summary = "Get all tickets for a property",
            description = "Retrieve all maintenance tickets related to a specific property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "404", description = "Property not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<List<MaintenanceTicketResponseDTO>>> getTicketsByProperty(
            @PathVariable
            @Parameter(description = "Property ID", required = true)
            Long propertyId) {

        List<MaintenanceTicketResponseDTO> tickets = maintenanceTicketService.getTicketsByProperty(propertyId);

        return ResponseEntity.ok(
                GeneralResponse.<List<MaintenanceTicketResponseDTO>>builder()
                        .message("Property tickets retrieved successfully")
                        .data(tickets)
                        .build());
    }

    @GetMapping("/tickets/landlord/{landlordId}")
    @Operation(summary = "Get all tickets for a landlord's properties",
            description = "Retrieve all maintenance tickets for a specific landlord")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "404", description = "Landlord not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<List<MaintenanceTicketResponseDTO>>> getTicketsByLandlord(
            @PathVariable
            @Parameter(description = "Landlord ID", required = true)
            Long landlordId) {

        List<MaintenanceTicketResponseDTO> tickets = maintenanceTicketService.getTicketsByLandlord(landlordId);

        return ResponseEntity.ok(
                GeneralResponse.<List<MaintenanceTicketResponseDTO>>builder()
                        .message("Landlord tickets retrieved successfully")
                        .data(tickets)
                        .build());
    }

    @PutMapping("/tickets/{ticketId}/status")
    @Operation(summary = "Update ticket status",
            description = "Update the status of a maintenance ticket (Open, In Progress, Resolved) - Landlord only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status provided"),
            @ApiResponse(responseCode = "403", description = "Unauthorized - Only landlord can update status"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<MaintenanceTicketResponseDTO>> updateTicketStatus(
            @PathVariable
            @Parameter(description = "Ticket ID", required = true)
            Long ticketId,

            @RequestParam
            @Parameter(description = "New ticket status", required = true)
            TicketStatus status,

            @RequestParam
            @Parameter(description = "Landlord ID for authorization", required = true)
            Long landlordId) {

        MaintenanceTicketResponseDTO ticket = maintenanceTicketService.updateTicketStatus(ticketId, status, landlordId);

        return ResponseEntity.ok(
                GeneralResponse.<MaintenanceTicketResponseDTO>builder()
                        .message("Ticket status updated successfully")
                        .data(ticket)
                        .build());
    }

    @DeleteMapping("/tickets/{ticketId}")
    @Operation(summary = "Delete a maintenance ticket",
            description = "Delete a maintenance ticket and associated resources")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<String>> deleteTicket(
            @PathVariable
            @Parameter(description = "Ticket ID", required = true)
            Long ticketId) {

        maintenanceTicketService.deleteTicket(ticketId);

        return ResponseEntity.ok(
                GeneralResponse.<String>builder()
                        .message("Ticket deleted successfully")
                        .data("Ticket ID: " + ticketId)
                        .build());
    }
}

