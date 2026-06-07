package com.proyecto.proyectoncapas.services.kyc.impl;

import com.proyecto.proyectoncapas.dto.request.IdentityDocumentRequestDTO;
import com.proyecto.proyectoncapas.dto.response.IdentityDocumentResponseDTO;
import com.proyecto.proyectoncapas.entities.IdentityDocument;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.FileStorageException;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.IdentityDocumentRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.services.kyc.IdentityDocumentService;
import com.proyecto.proyectoncapas.utils.mappers.IdentityDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentityDocumentServiceImpl implements IdentityDocumentService {

    private final IdentityDocumentRepository identityDocumentRepository;
    private final UserRepository userRepository; // Inyectamos repositorio de usuarios

    @Override
    @Transactional
    public IdentityDocumentResponseDTO uploadDocument(IdentityDocumentRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        IdentityDocument doc = identityDocumentRepository.findByUserId(request.getUserId())
                .orElse(new IdentityDocument());

        try {
            if (request.getFile().isEmpty()) {
                throw new FileStorageException("Cannot upload an empty file.");
            }

            doc.setUser(user);
            doc.setDocumentType(request.getDocumentType().toUpperCase());
            doc.setDocumentNumber(request.getDocumentNumber());
            doc.setExpiryDate(request.getExpiryDate());
            doc.setFileName(request.getFile().getOriginalFilename());
            doc.setFileType(request.getFile().getContentType());
            doc.setFileData(request.getFile().getBytes());

        } catch (IOException e) {
            log.error("Failed to read bytes from uploaded file for user ID: {}", request.getUserId(), e);
            throw new FileStorageException("Could not store file. Please try again!", e);
        }

        doc = identityDocumentRepository.save(doc);
        user.setIsVerified(true);
        userRepository.save(user);

        log.info("Binary KYC document safely stored in Database for User: {}", user.getEmail());
        return IdentityDocumentMapper.toDTO(doc);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityDocument getRawDocumentEntityByUserId(Long userId) {
        return identityDocumentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC Document not found for User ID: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityDocumentResponseDTO getDocumentByUserId(Long userId) {
        IdentityDocument doc = identityDocumentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC Document metadata not found for User ID: " + userId));
        return IdentityDocumentMapper.toDTO(doc);
    }
}