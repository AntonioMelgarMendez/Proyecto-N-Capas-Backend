package com.proyecto.proyectoncapas.services.photo.impl;

import com.proyecto.proyectoncapas.dto.response.PropertyPhotoResponseDTO;
import com.proyecto.proyectoncapas.entities.Property;
import com.proyecto.proyectoncapas.entities.PropertyPhoto;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.PropertyPhotoRepository;
import com.proyecto.proyectoncapas.repository.PropertyRepository;
import com.proyecto.proyectoncapas.services.photo.PropertyPhotoService;
import com.proyecto.proyectoncapas.services.s3.S3Service;
import com.proyecto.proyectoncapas.utils.mappers.PropertyPhotoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PropertyPhotoServiceImpl implements PropertyPhotoService {

    private final PropertyPhotoRepository propertyPhotoRepository;
    private final PropertyRepository propertyRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public PropertyPhotoResponseDTO uploadPhoto(Long propertyId, MultipartFile file, boolean isPrimary) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        String s3Key = s3Service.uploadFile(file, propertyId);
        String s3Url = s3Service.getFileUrl(s3Key);

        PropertyPhoto photo = PropertyPhoto.builder()
                .property(property)
                .s3Key(s3Key)
                .s3Url(s3Url)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .isPrimary(isPrimary)
                .build();

        photo = propertyPhotoRepository.save(photo);
        log.info("Photo uploaded for Property ID: {}, S3 key: {}", propertyId, s3Key);
        
        PropertyPhotoResponseDTO responseDTO = PropertyPhotoMapper.toResponseDTO(photo);
        responseDTO.setS3Url(s3Url);
        return responseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyPhotoResponseDTO> getPhotosByProperty(Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResourceNotFoundException("Property not found with ID: " + propertyId);
        }
        return propertyPhotoRepository.findByPropertyId(propertyId).stream()
                .map(photo -> {
                    PropertyPhotoResponseDTO dto = PropertyPhotoMapper.toResponseDTO(photo);
                    dto.setS3Url(s3Service.getFileUrl(photo.getS3Key()));
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public void deletePhoto(Long photoId) {
        PropertyPhoto photo = propertyPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with ID: " + photoId));

        s3Service.deleteFile(photo.getS3Key());
        propertyPhotoRepository.deleteById(photoId);
        log.info("Photo deleted with ID: {}, S3 key: {}", photoId, photo.getS3Key());
    }
}
