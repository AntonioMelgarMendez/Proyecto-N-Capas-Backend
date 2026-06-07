package com.proyecto.proyectoncapas.controller.property;

import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.PropertyPhotoResponseDTO;
import com.proyecto.proyectoncapas.services.photo.PropertyPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyPhotoController {

    private final PropertyPhotoService propertyPhotoService;

    @PostMapping(value = "/{propertyId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<PropertyPhotoResponseDTO>> uploadPhoto(
            @PathVariable Long propertyId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        PropertyPhotoResponseDTO data = propertyPhotoService.uploadPhoto(propertyId, file, isPrimary);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.<PropertyPhotoResponseDTO>builder()
                        .message("Photo uploaded successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{propertyId}/photos")
    public ResponseEntity<GeneralResponse<List<PropertyPhotoResponseDTO>>> getPhotosByProperty(
            @PathVariable Long propertyId) {
        List<PropertyPhotoResponseDTO> data = propertyPhotoService.getPhotosByProperty(propertyId);
        return ResponseEntity.ok(
                GeneralResponse.<List<PropertyPhotoResponseDTO>>builder()
                        .message("Photos retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<GeneralResponse<Void>> deletePhoto(@PathVariable Long photoId) {
        propertyPhotoService.deletePhoto(photoId);
        return ResponseEntity.ok(
                GeneralResponse.<Void>builder()
                        .message("Photo deleted successfully")
                        .build()
        );
    }
}
