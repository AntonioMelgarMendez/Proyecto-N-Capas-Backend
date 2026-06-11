package com.proyecto.proyectoncapas.dto.response;

import com.proyecto.proyectoncapas.utils.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAndPropertyResponseDTO {

    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private BigDecimal totalAmount;
    private ReservationStatus status;
    private BigDecimal basePricePerNight;

}