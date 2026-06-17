package com.proyecto.proyectoncapas.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TicketPhotoRequestDTO {

    @NotNull(message = "Photo file is required")
    private MultipartFile photo;
}
