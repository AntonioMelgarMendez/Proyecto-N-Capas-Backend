package com.proyecto.proyectoncapas.controller.maintenance;

import com.proyecto.proyectoncapas.dto.request.MaintenanceTicketRequestDTO;
import com.proyecto.proyectoncapas.dto.request.MaintenanceTicketUpdateRequestDTO;
import com.proyecto.proyectoncapas.dto.request.TicketPhotoRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.MaintenanceTicketResponseDTO;
import com.proyecto.proyectoncapas.dto.response.TicketPhotoResponseDTO;
import com.proyecto.proyectoncapas.services.maintenance.MaintenanceTicketService;
import com.proyecto.proyectoncapas.utils.enums.TicketStatus;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@Tag(name = "Maintenance Tickets", description = "Endpoints for managing maintenance tickets with photo support")
public class MaintenanceTicketController {

    private final MaintenanceTicketService maintenanceTicketService;

    @PostMapping("/tickets")
    @Operation(summary = "Create a new maintenance ticket",
            description = "Create a maintenance ticket. Upload photos separately via POST /tickets/{ticketId}/photos")
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

            @Valid @RequestBody
            @Parameter(description = "Maintenance ticket details", required = true)
            MaintenanceTicketRequestDTO request) {

        MaintenanceTicketResponseDTO ticket = maintenanceTicketService.createTicket(tenantId, request);

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
            description = "Retrieve all maintenance tickets created by a specific tenant, optionally filtered by status")
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
            Long tenantId,

            @RequestParam(required = false)
            @Parameter(description = "Filter by ticket status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)")
            TicketStatus status) {

        List<MaintenanceTicketResponseDTO> tickets = maintenanceTicketService.getTicketsByTenant(tenantId, status);

        return ResponseEntity.ok(
                GeneralResponse.<List<MaintenanceTicketResponseDTO>>builder()
                        .message("Tickets retrieved successfully")
                        .data(tickets)
                        .build());
    }

    @GetMapping("/tickets/property/{propertyId}")
    @Operation(summary = "Get all tickets for a property",
            description = "Retrieve all maintenance tickets related to a specific property, optionally filtered by status")
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
            Long propertyId,

            @RequestParam(required = false)
            @Parameter(description = "Filter by ticket status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)")
            TicketStatus status) {

        List<MaintenanceTicketResponseDTO> tickets = maintenanceTicketService.getTicketsByProperty(propertyId, status);

        return ResponseEntity.ok(
                GeneralResponse.<List<MaintenanceTicketResponseDTO>>builder()
                        .message("Property tickets retrieved successfully")
                        .data(tickets)
                        .build());
    }

    @GetMapping("/tickets/landlord/{landlordId}")
    @Operation(summary = "Get all tickets for a landlord's properties",
            description = "Retrieve all maintenance tickets for a specific landlord, optionally filtered by status")
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
            Long landlordId,

            @RequestParam(required = false)
            @Parameter(description = "Filter by ticket status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)")
            TicketStatus status) {

        List<MaintenanceTicketResponseDTO> tickets = maintenanceTicketService.getTicketsByLandlord(landlordId, status);

        return ResponseEntity.ok(
                GeneralResponse.<List<MaintenanceTicketResponseDTO>>builder()
                        .message("Landlord tickets retrieved successfully")
                        .data(tickets)
                        .build());
    }

    @PutMapping("/tickets/{ticketId}")
    @Operation(summary = "Update ticket details",
            description = "Update the title, description, or priority of a maintenance ticket - Tenant only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<MaintenanceTicketResponseDTO>> updateTicket(
            @PathVariable
            @Parameter(description = "Ticket ID", required = true)
            Long ticketId,

            @RequestBody
            @Parameter(description = "Fields to update (title, description, priority — all optional)", required = true)
            MaintenanceTicketUpdateRequestDTO request) {

        MaintenanceTicketResponseDTO ticket = maintenanceTicketService.updateTicket(ticketId, request);

        return ResponseEntity.ok(
                GeneralResponse.<MaintenanceTicketResponseDTO>builder()
                        .message("Ticket updated successfully")
                        .data(ticket)
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

    @PostMapping(value = "/tickets/{ticketId}/photos", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload evidence photo to a ticket",
            description = "Upload a photo of the damage or issue as evidence to an existing maintenance ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Photo uploaded successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file or request"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GeneralResponse<TicketPhotoResponseDTO>> uploadTicketPhoto(
            @PathVariable
            @Parameter(description = "Ticket ID", required = true)
            Long ticketId,

            @Valid @ModelAttribute
            TicketPhotoRequestDTO request) {

        TicketPhotoResponseDTO photo = maintenanceTicketService.uploadTicketPhoto(ticketId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GeneralResponse.<TicketPhotoResponseDTO>builder()
                        .message("Evidence photo uploaded successfully")
                        .data(photo)
                        .build());
    }
}

