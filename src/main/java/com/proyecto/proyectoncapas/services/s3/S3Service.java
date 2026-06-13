package com.proyecto.proyectoncapas.services.s3;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface S3Service {

    String uploadFile(MultipartFile file, Long propertyId);

    void deleteFile(String s3Key);

    String getFileUrl(String s3Key);
}
