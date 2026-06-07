package com.proyecto.proyectoncapas.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
public class IdentityDocumentRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Document type cannot be empty")
    private String documentType;

    @NotBlank(message = "Document number cannot be empty")
    private String documentNumber;

    @NotNull(message = "The file document is required")
    private MultipartFile file;

    @NotNull(message = "Expiry date is required")
    @Future(message = "The document must not be expired")
    private LocalDate expiryDate;
}