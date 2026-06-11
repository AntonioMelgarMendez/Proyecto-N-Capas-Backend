package com.proyecto.proyectoncapas.utils.mappers;

import com.proyecto.proyectoncapas.dto.response.PropertyPhotoResponseDTO;
import com.proyecto.proyectoncapas.entities.PropertyPhoto;

public class PropertyPhotoMapper {

    public static PropertyPhotoResponseDTO toResponseDTO(PropertyPhoto photo) {
        return PropertyPhotoResponseDTO.builder()
                .id(photo.getId())
                .propertyId(photo.getProperty().getId())
                .s3Url(photo.getS3Url())
                .fileName(photo.getFileName())
                .fileType(photo.getFileType())
                .isPrimary(photo.getIsPrimary())
                .uploadedAt(photo.getUploadedAt())
                .build();
    }
}
