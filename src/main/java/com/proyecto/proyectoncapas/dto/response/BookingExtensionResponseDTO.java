package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class BookingExtensionResponseDTO {
    private Long reservationId;
    private LocalDate checkInDate;
    private LocalDate newCheckOutDate;
    private BigDecimal amountPaidNow;
    private BigDecimal totalReservationPrice;

}