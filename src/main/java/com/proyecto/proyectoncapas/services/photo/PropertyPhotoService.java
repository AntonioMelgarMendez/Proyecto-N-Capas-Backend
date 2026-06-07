package com.proyecto.proyectoncapas.services.photo;

import com.proyecto.proyectoncapas.dto.response.PropertyPhotoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyPhotoService {

    PropertyPhotoResponseDTO uploadPhoto(Long propertyId, MultipartFile file, boolean isPrimary);

    List<PropertyPhotoResponseDTO> getPhotosByProperty(Long propertyId);

    void deletePhoto(Long photoId);
}
