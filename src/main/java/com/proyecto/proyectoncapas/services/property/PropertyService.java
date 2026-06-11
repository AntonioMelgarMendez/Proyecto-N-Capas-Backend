package com.proyecto.proyectoncapas.services.property;

import com.proyecto.proyectoncapas.dto.request.PropertyRequestDTO;
import com.proyecto.proyectoncapas.dto.response.PropertyResponseDTO;

import java.util.List;

public interface PropertyService {

    PropertyResponseDTO createProperty(PropertyRequestDTO request);

    PropertyResponseDTO updateProperty(Long id, PropertyRequestDTO request);

    PropertyResponseDTO getPropertyById(Long id);

    List<PropertyResponseDTO> getAllProperties();

    List<PropertyResponseDTO> getPropertiesByLandlord(Long landlordId);

    List<PropertyResponseDTO> getAvailableProperties();

    List<PropertyResponseDTO> getPropertiesByCity(String city);

    void deleteProperty(Long id);

    PropertyResponseDTO addRule(Long propertyId, String description);

    void deleteRule(Long ruleId);
}
