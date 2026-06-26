package com.proyecto.proyectoncapas.controller.kyc;

import com.proyecto.proyectoncapas.dto.request.IdentityDocumentRequestDTO;
import com.proyecto.proyectoncapas.dto.response.IdentityDocumentResponseDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.entities.IdentityDocument;
import com.proyecto.proyectoncapas.services.kyc.IdentityDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identity")
@RequiredArgsConstructor
@Tag(name = "KYC / Identity Documents", description = "Upload and retrieve identity documents for user verification")
public class IdentityDocumentController {

    private final IdentityDocumentService identityDocumentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload identity document", description = "Upload a KYC identity document (passport, DUI, etc.) as binary — marks the user as verified")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document uploaded and user verified"),
            @ApiResponse(responseCode = "400", description = "Empty or invalid file"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Get KYC metadata", description = "Retrieve identity document metadata for a user (does not return binary data)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metadata retrieved"),
            @ApiResponse(responseCode = "404", description = "Document not found for this user")
    })
    public ResponseEntity<GeneralResponse<IdentityDocumentResponseDTO>> getKYCMetadata(@PathVariable Long userId) {
        IdentityDocumentResponseDTO data = identityDocumentService.getDocumentByUserId(userId);
        GeneralResponse<IdentityDocumentResponseDTO> response = GeneralResponse.<IdentityDocumentResponseDTO>builder()
                .message("Identity data fetched successfully.")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{userId}")
    @Operation(summary = "Download identity document file", description = "Download the raw binary identity document file for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File returned as binary"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<byte[]> downloadDocumentFile(@PathVariable Long userId) {
        IdentityDocument doc = identityDocumentService.getRawDocumentEntityByUserId(userId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .body(doc.getFileData());
    }
}
