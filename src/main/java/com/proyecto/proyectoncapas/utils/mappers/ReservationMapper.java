package com.proyecto.proyectoncapas.utils.mappers;
import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ReservationResponseDTO;
import com.proyecto.proyectoncapas.entities.Reservation;

public class ReservationMapper {

    public static ReservationResponseDTO toResponseDTO(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .numberOfGuests(reservation.getNumberOfGuests())
                .totalAmount(reservation.getTotalAmount())
                .status(reservation.getStatus())
                .build();
    }

    public static Reservation toEntity(ReservationRequestDTO dto) {
        return Reservation.builder()
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .numberOfGuests(dto.getNumberOfGuests())
                .totalAmount(dto.getTotalAmount())
                .status("PENDING")
                .build();
    }
}