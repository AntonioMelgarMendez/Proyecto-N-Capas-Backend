package com.proyecto.proyectoncapas.services.reservation;

import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.*;
import com.proyecto.proyectoncapas.entities.Reservation;

import java.time.LocalDate;

import java.util.List;

public interface ReservationService {
    ReservationResponseDTO createBooking(Long propertyId, ReservationRequestDTO request);
    Reservation findById(Long id);
    BookingExtensionResponseDTO processExtensionPayment(Long reservationId, int extraDays);
    void cancelOrReleaseExpiredBooking(Long bookingId);
    CancellationQuoteResponseDTO quoteCancellation(Long reservationId);
    ExtensionQuoteResponseDTO quoteExtension(Long id, int extraDays);
    CancellationResponseDTO confirmCancellation(Long reservationId);
    ReservationQuoteResponseDTO calculateQuote(Long propertyId, ReservationRequestDTO request);
    List<TenantReservationResponseDTO> getTenantReservations(Long tenantId);
}
