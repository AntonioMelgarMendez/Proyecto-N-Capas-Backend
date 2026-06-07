package com.proyecto.proyectoncapas.controller.property;

import com.proyecto.proyectoncapas.dto.request.PropertyRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.PropertyResponseDTO;
import com.proyecto.proyectoncapas.services.property.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<GeneralResponse<PropertyResponseDTO>> createProperty(
            @Valid @RequestBody PropertyRequestDTO request) {
        PropertyResponseDTO data = propertyService.createProperty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.<PropertyResponseDTO>builder()
                        .message("Property created successfully")
                        .data(data)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<PropertyResponseDTO>> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequestDTO request) {
        PropertyResponseDTO data = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(
                GeneralResponse.<PropertyResponseDTO>builder()
                        .message("Property updated successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<PropertyResponseDTO>> getPropertyById(@PathVariable Long id) {
        PropertyResponseDTO data = propertyService.getPropertyById(id);
        return ResponseEntity.ok(
                GeneralResponse.<PropertyResponseDTO>builder()
                        .message("Property retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<PropertyResponseDTO>>> getAllProperties() {
        List<PropertyResponseDTO> data = propertyService.getAllProperties();
        return ResponseEntity.ok(
                GeneralResponse.<List<PropertyResponseDTO>>builder()
                        .message("Properties retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<GeneralResponse<List<PropertyResponseDTO>>> getPropertiesByLandlord(
            @PathVariable Long landlordId) {
        List<PropertyResponseDTO> data = propertyService.getPropertiesByLandlord(landlordId);
        return ResponseEntity.ok(
                GeneralResponse.<List<PropertyResponseDTO>>builder()
                        .message("Properties retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/available")
    public ResponseEntity<GeneralResponse<List<PropertyResponseDTO>>> getAvailableProperties() {
        List<PropertyResponseDTO> data = propertyService.getAvailableProperties();
        return ResponseEntity.ok(
                GeneralResponse.<List<PropertyResponseDTO>>builder()
                        .message("Available properties retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<GeneralResponse<List<PropertyResponseDTO>>> getPropertiesByCity(
            @PathVariable String city) {
        List<PropertyResponseDTO> data = propertyService.getPropertiesByCity(city);
        return ResponseEntity.ok(
                GeneralResponse.<List<PropertyResponseDTO>>builder()
                        .message("Properties retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok(
                GeneralResponse.<Void>builder()
                        .message("Property deleted successfully")
                        .build()
        );
    }

    @PostMapping("/{propertyId}/rules")
    public ResponseEntity<GeneralResponse<PropertyResponseDTO>> addRule(
            @PathVariable Long propertyId,
            @RequestParam String description) {
        PropertyResponseDTO data = propertyService.addRule(propertyId, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.<PropertyResponseDTO>builder()
                        .message("Rule added successfully")
                        .data(data)
                        .build()
        );
    }

    @DeleteMapping("/rules/{ruleId}")
    public ResponseEntity<GeneralResponse<Void>> deleteRule(@PathVariable Long ruleId) {
        propertyService.deleteRule(ruleId);
        return ResponseEntity.ok(
                GeneralResponse.<Void>builder()
                        .message("Rule deleted successfully")
                        .build()
        );
    }
}
