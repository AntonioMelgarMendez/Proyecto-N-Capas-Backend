package com.proyecto.proyectoncapas.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent
    private LocalDate checkInDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Future
    private LocalDate checkOutDate;

    @NotNull
    @Min(1)
    private Integer numberOfGuests;

    private BigDecimal totalAmount;

    private boolean includeCleaning = true;
    private boolean includeInsurance = false;

    @NotNull
    private Long propertyId;
    @NotNull
    private Long tenantId;

}