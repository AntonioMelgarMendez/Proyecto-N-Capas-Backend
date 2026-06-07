package com.proyecto.proyectoncapas.controller.kyc;

import com.proyecto.proyectoncapas.dto.request.IdentityDocumentRequestDTO;
import com.proyecto.proyectoncapas.dto.response.IdentityDocumentResponseDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.entities.IdentityDocument;
import com.proyecto.proyectoncapas.services.kyc.IdentityDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identity")
@RequiredArgsConstructor
public class IdentityDocumentController {

    private final IdentityDocumentService identityDocumentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<IdentityDocumentResponseDTO>> uploadKYCDocument(
            @Valid @ModelAttribute IdentityDocumentRequestDTO request) {

        IdentityDocumentResponseDTO data = identityDocumentService.uploadDocument(request);

        GeneralResponse<IdentityDocumentResponseDTO> response = GeneralResponse.<IdentityDocumentResponseDTO>builder()
                .message("Binary documentation successfully uploaded and saved in database.")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse<IdentityDocumentResponseDTO>> getKYCMetadata(@PathVariable Long userId) {
        IdentityDocumentResponseDTO data = identityDocumentService.getDocumentByUserId(userId);
        GeneralResponse<IdentityDocumentResponseDTO> response = GeneralResponse.<IdentityDocumentResponseDTO>builder()
                .message("Identity data fetched successfully.")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{userId}")
    public ResponseEntity<byte[]> downloadDocumentFile(@PathVariable Long userId) {
        IdentityDocument doc = identityDocumentService.getRawDocumentEntityByUserId(userId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .body(doc.getFileData());
    }
}