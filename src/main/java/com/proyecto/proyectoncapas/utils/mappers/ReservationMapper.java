package com.proyecto.proyectoncapas.utils.mappers;
import com.proyecto.proyectoncapas.dto.request.ReservationRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ReservationAndPropertyResponseDTO;
import com.proyecto.proyectoncapas.dto.response.ReservationResponseDTO;
import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.utils.enums.ReservationStatus;

public class ReservationMapper {

    public static ReservationResponseDTO toResponseDTO(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .numberOfGuests(reservation.getNumberOfGuests())
                .totalAmount(reservation.getTotalAmount())
                .status(String.valueOf(reservation.getStatus()))
                .build();
    }

    public static ReservationAndPropertyResponseDTO toResponseDTOProperty(Reservation reservation) {
        return ReservationAndPropertyResponseDTO.builder()
                .id(reservation.getId())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .numberOfGuests(reservation.getNumberOfGuests())
                .totalAmount(reservation.getTotalAmount())
                .status(reservation.getStatus())
                .basePricePerNight(reservation.getProperty().getPricePerNight())
                .build();
    }

    public static Reservation toEntity(ReservationRequestDTO dto) {
        return Reservation.builder()
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .numberOfGuests(dto.getNumberOfGuests())
                .totalAmount(dto.getTotalAmount())
                .status(ReservationStatus.PENDING_PAYMENT)
                .build();
    }
}