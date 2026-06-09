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
                .status(reservation.getStatus())
                .build();
    }

}