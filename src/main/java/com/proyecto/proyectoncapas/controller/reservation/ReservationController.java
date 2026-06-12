package com.proyecto.proyectoncapas.controller.reservation;

import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.*;
import com.proyecto.proyectoncapas.exception.InvalidPaymentStateException;
import com.proyecto.proyectoncapas.services.reservation.AvailabilityService;
import com.proyecto.proyectoncapas.services.reservation.ReservationService;
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
public class ReservationController {

    private final AvailabilityService availabilityService;
    private final ReservationService bookingService;

    @GetMapping("/{id}/calendar")
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
    public ResponseEntity<GeneralResponse<String>> payExtension(
            @PathVariable Long id,
            @RequestParam int extraDays) {

        throw new InvalidPaymentStateException(
                "Direct extension payment is disabled. Request approval and use POST /api/payments/checkout/extension/{extensionRequestId}"
        );
    }

    @PostMapping("/{id}/cancel/quote")
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
    public ResponseEntity<GeneralResponse<List<TenantReservationResponseDTO>>> getTenantReservations(@PathVariable Long tenantId) {
        List<TenantReservationResponseDTO> list = bookingService.getTenantReservations(tenantId);
        return ResponseEntity.ok(
                GeneralResponse.<List<TenantReservationResponseDTO>>builder()
                        .message("Reservations retrieved successfully")
                        .data(list)
                        .build()
        );
    }
}
