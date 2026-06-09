package com.proyecto.proyectoncapas.services.reservation;

import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.*;
import com.proyecto.proyectoncapas.entities.Reservation;

import java.time.LocalDate;

public interface ReservationService {
    ReservationResponseDTO createBooking(Long propertyId, ReservationRequestDTO request);
    Reservation findById(Long id);
    BookingExtensionResponseDTO processExtensionPayment(Long reservationId, int extraDays);
    void cancelOrReleaseExpiredBooking(Long bookingId);
    CancellationQuoteResponseDTO quoteCancellation(Long reservationId);
    CancellationResponseDTO confirmCancellation(Long reservationId);
}
