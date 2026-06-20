package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporalKeyResponseDTO {
    private Long tempKeyId;
    private String pin; // este pin solo se devuelve durante la generación. nunca se persiste asi
    private String roomPin;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean used;
    private Boolean revoked;
}
