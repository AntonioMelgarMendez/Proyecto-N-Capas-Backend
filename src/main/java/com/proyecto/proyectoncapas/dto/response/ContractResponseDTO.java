package com.proyecto.proyectoncapas.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ContractResponseDTO {
    private Long contractId;
    private Long reservationId;
    private String content;
    private String signatureHash;
    private LocalDateTime tenantSignatureDate;
    private LocalDateTime landlordSignatureDate;
    private String status;
}