package com.proyecto.proyectoncapas.controller.property;

import com.proyecto.proyectoncapas.dto.request.PropertyRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.PropertyResponseDTO;
import com.proyecto.proyectoncapas.services.property.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Tag(name = "Properties", description = "CRUD operations for property listings and pricing rules")
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    @Operation(summary = "Create property", description = "Create a new rental property listing for a landlord")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Property created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Landlord not found")
    })
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
    @Operation(summary = "Update property", description = "Update title, description, address, price and other details of an existing property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property updated"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
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
    @Operation(summary = "Get property by ID", description = "Retrieve full details of a single property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property found"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
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
    @Operation(summary = "Get all properties", description = "Retrieve all property listings on the platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned")
    })
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
    @Operation(summary = "Get properties by landlord", description = "Retrieve all properties belonging to a specific landlord")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned")
    })
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
    @Operation(summary = "Get available properties", description = "Retrieve all properties currently marked as available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned")
    })
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
    @Operation(summary = "Get properties by city", description = "Retrieve all properties in a given city (case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned")
    })
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
    @Operation(summary = "Delete property", description = "Delete a property. Cannot delete if active reservations exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property deleted"),
            @ApiResponse(responseCode = "404", description = "Property not found"),
            @ApiResponse(responseCode = "409", description = "Property has existing reservations")
    })
    public ResponseEntity<GeneralResponse<Void>> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok(
                GeneralResponse.<Void>builder()
                        .message("Property deleted successfully")
                        .build()
        );
    }

    @PostMapping("/{propertyId}/rules")
    @Operation(summary = "Add pricing rule", description = "Add a pricing rule to a property (e.g. cleaning fee, discount)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rule added"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
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
    @Operation(summary = "Delete pricing rule", description = "Remove a pricing rule from a property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rule deleted"),
            @ApiResponse(responseCode = "404", description = "Rule not found")
    })
    public ResponseEntity<GeneralResponse<Void>> deleteRule(@PathVariable Long ruleId) {
        propertyService.deleteRule(ruleId);
        return ResponseEntity.ok(
                GeneralResponse.<Void>builder()
                        .message("Rule deleted successfully")
                        .build()
        );
    }
}
