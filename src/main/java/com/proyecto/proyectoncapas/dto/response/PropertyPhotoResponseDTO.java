package com.proyecto.proyectoncapas.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyPhotoResponseDTO {

    private Long id;
    private Long propertyId;
    private String s3Url;
    private String fileName;
    private String fileType;
    private Boolean isPrimary;
    private LocalDateTime uploadedAt;
}
