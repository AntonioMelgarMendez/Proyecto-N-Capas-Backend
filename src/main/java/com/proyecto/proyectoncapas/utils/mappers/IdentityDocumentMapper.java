package com.proyecto.proyectoncapas.utils.mappers;
import com.proyecto.proyectoncapas.dto.response.IdentityDocumentResponseDTO;
import com.proyecto.proyectoncapas.entities.IdentityDocument;

public class IdentityDocumentMapper {

    public static IdentityDocumentResponseDTO toDTO(IdentityDocument entity) {
        return IdentityDocumentResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .documentType(entity.getDocumentType())
                .documentNumber(entity.getDocumentNumber())
                .fileName(entity.getFileName())
                .fileType(entity.getFileType())
                .downloadUrl("/api/identity/download/" + entity.getUser().getId())
                .expiryDate(entity.getExpiryDate())
                .isVerified(entity.getIsVerified())
                .verifiedAt(entity.getVerifiedAt())
                .build();
    }
}