package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporalKeyResponseDTO {
    private Long reservationId;
    private String pin; // este pin solo se devuelve durante la generación. nunca se persiste asi
    private LocalDate validFrom;
    private LocalDate validUntil;
}
