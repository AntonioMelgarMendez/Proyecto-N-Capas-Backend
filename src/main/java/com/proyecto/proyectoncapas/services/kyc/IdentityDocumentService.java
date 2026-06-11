package com.proyecto.proyectoncapas.services.kyc;

import com.proyecto.proyectoncapas.dto.request.IdentityDocumentRequestDTO;
import com.proyecto.proyectoncapas.dto.response.IdentityDocumentResponseDTO;
import com.proyecto.proyectoncapas.entities.IdentityDocument;

public interface IdentityDocumentService {
    IdentityDocumentResponseDTO uploadDocument(IdentityDocumentRequestDTO request);
    IdentityDocumentResponseDTO getDocumentByUserId(Long userId);
    IdentityDocument getRawDocumentEntityByUserId(Long userId);
}