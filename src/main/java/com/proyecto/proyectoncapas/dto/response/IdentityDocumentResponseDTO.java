package com.proyecto.proyectoncapas.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class IdentityDocumentResponseDTO {
    private Long id;
    private Long userId;
    private String documentType;
    private String documentNumber;
    private String fileName;
    private String fileType;
    private String downloadUrl;
    private LocalDate expiryDate;
    private Boolean isVerified;
    private LocalDateTime verifiedAt;
}