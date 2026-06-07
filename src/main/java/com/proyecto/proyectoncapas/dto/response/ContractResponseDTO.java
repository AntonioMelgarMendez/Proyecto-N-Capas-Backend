package com.proyecto.proyectoncapas.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ContractResponseDTO {
    private Long contractId;
    private Long reservationId;
    private String signatureHash;
    private LocalDateTime signedAt;
    private String status;
}