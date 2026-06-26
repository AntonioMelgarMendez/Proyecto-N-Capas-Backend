package com.proyecto.proyectoncapas.controller.property;

import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.PropertyPhotoResponseDTO;
import com.proyecto.proyectoncapas.services.photo.PropertyPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Property Photos", description = "Upload and manage photos for property listings")
public class PropertyPhotoController {

    private final PropertyPhotoService propertyPhotoService;

    @PostMapping(value = "/{propertyId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload property photo", description = "Upload a photo (JPEG/PNG/WEBP, max 5MB) for a property listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Photo uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
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
    @Operation(summary = "Get property photos", description = "Retrieve all photos for a property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photos retrieved"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
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
    @Operation(summary = "Delete property photo", description = "Delete a photo and remove it from S3 storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo deleted"),
            @ApiResponse(responseCode = "404", description = "Photo not found")
    })
    public ResponseEntity<GeneralResponse<Void>> deletePhoto(@PathVariable Long photoId) {
        propertyPhotoService.deletePhoto(photoId);
        return ResponseEntity.ok(
                GeneralResponse.<Void>builder()
                        .message("Photo deleted successfully")
                        .build()
        );
    }
}
