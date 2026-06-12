package com.proyecto.proyectoncapas.services.reservation;

import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.*;
import com.proyecto.proyectoncapas.entities.Reservation;

import java.math.BigDecimal;
import java.util.List;

public interface ReservationService {
    ReservationResponseDTO createBooking(Long propertyId, ReservationRequestDTO request);
    Reservation findById(Long id);
    void cancelOrReleaseExpiredBooking(Long bookingId);
    CancellationQuoteResponseDTO quoteCancellation(Long reservationId);
    ExtensionQuoteResponseDTO quoteExtension(Long id, int extraDays);
    ExtensionRequestResponseDTO requestExtension(Long reservationId, int extraDays);
    ExtensionRequestResponseDTO approveExtension(Long requestId, Long landlordId);
    ExtensionRequestResponseDTO rejectExtension(Long requestId, Long landlordId);
    BookingExtensionResponseDTO applyApprovedExtension(Long extensionRequestId, BigDecimal paidAmount);
    CancellationResponseDTO confirmCancellation(Long reservationId);
    ReservationQuoteResponseDTO calculateQuote(Long propertyId, ReservationRequestDTO request);
    List<TenantReservationResponseDTO> getTenantReservations(Long tenantId);
    List<ExtensionRequestResponseDTO> getExtensionRequestsByReservation(Long reservationId);
    List<ExtensionRequestLandlordResponseDTO> getLandlordExtensionRequests(Long landlordId, String status);
}
