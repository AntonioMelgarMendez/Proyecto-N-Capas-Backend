package com.proyecto.proyectoncapas.controller.reservation;

import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.*;
import com.proyecto.proyectoncapas.exception.InvalidPaymentStateException;
import com.proyecto.proyectoncapas.services.reservation.AvailabilityService;
import com.proyecto.proyectoncapas.services.reservation.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/reservations")
@AllArgsConstructor
@Tag(name = "Reservations", description = "Full booking lifecycle — availability calendar, pricing quotes, extensions, and cancellations")
public class ReservationController {

    private final AvailabilityService availabilityService;
    private final ReservationService bookingService;

    @GetMapping("/{id}/calendar")
    @Operation(summary = "Get availability calendar", description = "Returns all occupied dates for a property within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Occupied dates returned")
    })
    public ResponseEntity<GeneralResponse<List<LocalDate>>> getPropertyCalendar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        List<LocalDate> occupiedDates = availabilityService.getOccupiedCalendar(id, start, end);
        return ResponseEntity.ok(
                GeneralResponse.<List<LocalDate>>builder()
                        .message("Calendar retrieved successfully")
                        .data(occupiedDates)
                        .build()
        );
    }

    @PostMapping("/{id}/book")
    @Operation(summary = "Create booking", description = "Book a property for specific dates. Reservation is created in PENDING_PAYMENT status — payment must be initiated next.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking created"),
            @ApiResponse(responseCode = "409", description = "Dates unavailable or invalid")
    })
    public ResponseEntity<GeneralResponse<ReservationResponseDTO>> bookProperty(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {

        ReservationResponseDTO newBooking = bookingService.createBooking(id, reservationRequestDTO);

        return ResponseEntity.ok(
                GeneralResponse.<ReservationResponseDTO>builder()
                        .message("Book Successfully Created")
                        .data(newBooking)
                        .build()
        );
    }

    @PostMapping("/{id}/quote")
    @Operation(summary = "Get price quote", description = "Calculate the total price for a reservation including applicable rules (cleaning fee, discounts, etc.) without creating a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quote calculated"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<GeneralResponse<ReservationQuoteResponseDTO>> quoteReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {

        ReservationQuoteResponseDTO quote = bookingService.calculateQuote(id, reservationRequestDTO);

        return ResponseEntity.ok(
                GeneralResponse.<ReservationQuoteResponseDTO>builder()
                        .message("Reservation Quote Calculated Successfully")
                        .data(quote)
                        .build()
        );
    }

    @PostMapping("/{id}/extend/quote")
    @Operation(summary = "Quote stay extension", description = "Get the cost breakdown for extending a reservation by N extra days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extension quote returned"),
            @ApiResponse(responseCode = "409", description = "Reservation is not in an extendable status")
    })
    public ResponseEntity<GeneralResponse<ExtensionQuoteResponseDTO>> quoteExtension(
            @PathVariable Long id,
            @RequestParam int extraDays) {

        ExtensionQuoteResponseDTO data = bookingService.quoteExtension(id, extraDays);

        return ResponseEntity.ok(
                GeneralResponse.<ExtensionQuoteResponseDTO>builder()
                        .message("Extension Information")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/{id}/extend/request")
    @Operation(summary = "Request stay extension", description = "Submit an extension request to the landlord. Landlord must approve before payment can be made.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extension request submitted"),
            @ApiResponse(responseCode = "409", description = "Active extension request already exists")
    })
    public ResponseEntity<GeneralResponse<ExtensionRequestResponseDTO>> requestExtension(
            @PathVariable Long id,
            @RequestParam int extraDays) {

        ExtensionRequestResponseDTO data = bookingService.requestExtension(id, extraDays);

        return ResponseEntity.ok(
                GeneralResponse.<ExtensionRequestResponseDTO>builder()
                        .message("Extension request submitted successfully")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/extend/{requestId}/approve")
    @Operation(summary = "Approve extension request", description = "Landlord approves a tenant's extension request. Tenant can then pay via /api/payments/checkout/extension/{requestId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extension approved"),
            @ApiResponse(responseCode = "403", description = "Only the property landlord can approve"),
            @ApiResponse(responseCode = "409", description = "Request is not pending")
    })
    public ResponseEntity<GeneralResponse<ExtensionRequestResponseDTO>> approveExtension(
            @PathVariable Long requestId,
            @RequestParam Long landlordId) {

        ExtensionRequestResponseDTO data = bookingService.approveExtension(requestId, landlordId);

        return ResponseEntity.ok(
                GeneralResponse.<ExtensionRequestResponseDTO>builder()
                        .message("Extension request approved")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/extend/{requestId}/reject")
    @Operation(summary = "Reject extension request", description = "Landlord rejects a tenant's extension request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extension rejected"),
            @ApiResponse(responseCode = "409", description = "Request is not pending")
    })
    public ResponseEntity<GeneralResponse<ExtensionRequestResponseDTO>> rejectExtension(
            @PathVariable Long requestId,
            @RequestParam Long landlordId) {

        ExtensionRequestResponseDTO data = bookingService.rejectExtension(requestId, landlordId);

        return ResponseEntity.ok(
                GeneralResponse.<ExtensionRequestResponseDTO>builder()
                        .message("Extension request rejected")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/extend/landlord/{landlordId}")
    @Operation(summary = "Get landlord extension requests", description = "Retrieve all extension requests for a landlord's properties, optionally filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extension requests returned")
    })
    public ResponseEntity<GeneralResponse<List<ExtensionRequestLandlordResponseDTO>>> getLandlordExtensionRequests(
            @PathVariable Long landlordId,
            @RequestParam(required = false) String status) {

        List<ExtensionRequestLandlordResponseDTO> data = bookingService.getLandlordExtensionRequests(landlordId, status);

        return ResponseEntity.ok(
                GeneralResponse.<List<ExtensionRequestLandlordResponseDTO>>builder()
                        .message("Extension requests retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{reservationId}/extend/requests")
    @Operation(summary = "Get extension requests by reservation", description = "Retrieve all extension requests for a specific reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extension requests returned")
    })
    public ResponseEntity<GeneralResponse<List<ExtensionRequestResponseDTO>>> getExtensionRequestsByReservation(
            @PathVariable Long reservationId) {

        List<ExtensionRequestResponseDTO> data = bookingService.getExtensionRequestsByReservation(reservationId);

        return ResponseEntity.ok(
                GeneralResponse.<List<ExtensionRequestResponseDTO>>builder()
                        .message("Extension requests retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/{id}/extend/pay")
    @Operation(summary = "Pay extension (disabled)", description = "Direct extension payment is disabled. Use POST /api/payments/checkout/extension/{extensionRequestId} after landlord approval.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Direct extension payment is disabled")
    })
    public ResponseEntity<GeneralResponse<String>> payExtension(
            @PathVariable Long id,
            @RequestParam int extraDays) {

        throw new InvalidPaymentStateException(
                "Direct extension payment is disabled. Request approval and use POST /api/payments/checkout/extension/{extensionRequestId}"
        );
    }

    @PostMapping("/{id}/cancel/quote")
    @Operation(summary = "Quote cancellation", description = "Calculate the penalty fee and refund amount before confirming a cancellation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancellation quote returned"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<GeneralResponse<CancellationQuoteResponseDTO>> quoteCancellation(@PathVariable Long id) {

        CancellationQuoteResponseDTO data = bookingService.quoteCancellation(id);

        return ResponseEntity.ok(
                GeneralResponse.<CancellationQuoteResponseDTO>builder()
                        .message("Cancellation Quote Information")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/{id}/cancel/confirm")
    @Operation(summary = "Confirm cancellation", description = "Cancel a confirmed reservation, apply the cancellation penalty, and process a partial refund if applicable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation cancelled"),
            @ApiResponse(responseCode = "409", description = "Reservation cannot be cancelled in its current status")
    })
    public ResponseEntity<GeneralResponse<CancellationResponseDTO>> confirmCancellation(@PathVariable Long id) {

        CancellationResponseDTO data = bookingService.confirmCancellation(id);

        return ResponseEntity.ok(
                GeneralResponse.<CancellationResponseDTO>builder()
                        .message("Reservation cancelled successfully. Calendar days released.")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get tenant reservations", description = "Retrieve all reservations for a tenant with contract status and entry PIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations returned")
    })
    public ResponseEntity<GeneralResponse<List<TenantReservationResponseDTO>>> getTenantReservations(@PathVariable Long tenantId) {
        List<TenantReservationResponseDTO> list = bookingService.getTenantReservations(tenantId);
        return ResponseEntity.ok(
                GeneralResponse.<List<TenantReservationResponseDTO>>builder()
                        .message("Reservations retrieved successfully")
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/landlord/{landlordId}/tenants")
    @Operation(summary = "Get landlord tenants", description = "Distinct tenants with reservations on the landlord's properties")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenants returned")
    })
    public ResponseEntity<GeneralResponse<List<UserResponseDTO>>> getLandlordTenants(@PathVariable Long landlordId) {
        List<UserResponseDTO> tenants = bookingService.getLandlordTenants(landlordId);
        return ResponseEntity.ok(
                GeneralResponse.<List<UserResponseDTO>>builder()
                        .message("Tenants retrieved successfully")
                        .data(tenants)
                        .build()
        );
    }
}
