package com.proyecto.proyectoncapas.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @PastOrPresent
    private LocalDate checkInDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Past
    private LocalDate checkOutDate;

    @NotNull
    @Min(1)
    private Integer numberOfGuests;

    private boolean includeCleaning = true; // Por defecto true, por si no lo envían
    private boolean includeInsurance = false;

    @NotNull
    private Long propertyId;
    @NotNull
    private Long tenantId;
}