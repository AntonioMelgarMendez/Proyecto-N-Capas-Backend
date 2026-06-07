package com.proyecto.proyectoncapas.utils.mappers;
import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ReservationResponseDTO;
import com.proyecto.proyectoncapas.entities.Reservation;

public class ReservationMapper {

    // De Entidad a DTO de Respuesta
    public static ReservationResponseDTO toResponseDTO(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .checkIn(reservation.getCheckIn())
                .checkOut(reservation.getCheckOut())
                .totalAmount(reservation.getTotalAmount())
                .paymentStatus(reservation.getPaymentStatus())
                .stripeSessionId(reservation.getStripeSessionId())
                .build();
    }

    // De DTO de Petición a Entidad
    public static Reservation toEntity(ReservationRequestDTO dto) {
        return Reservation.builder()
                .checkIn(dto.getCheckIn())
                .checkOut(dto.getCheckOut())
                .totalAmount(dto.getTotalAmount())
                .paymentStatus("PENDING")
                .build();
    }
}