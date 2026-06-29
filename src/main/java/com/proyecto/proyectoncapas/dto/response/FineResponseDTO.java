package com.proyecto.proyectoncapas.dto.response;

import com.proyecto.proyectoncapas.utils.enums.FineStatus;
import com.proyecto.proyectoncapas.utils.enums.InfractionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineResponseDTO {
    private Long fineId;
    private Long userId;
    private String userFullName;
    private Long generatedById;
    private String generatedByFullName;
    private InfractionType infractionType;
    private FineStatus status;
    private BigDecimal amount;
    private String description;
    private LocalDateTime infractionDate;
    private LocalDateTime creationDate;
}
