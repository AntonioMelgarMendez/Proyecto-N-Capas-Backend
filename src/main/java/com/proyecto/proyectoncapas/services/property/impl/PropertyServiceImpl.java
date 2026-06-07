package com.proyecto.proyectoncapas.services.property.impl;

import com.proyecto.proyectoncapas.dto.request.PropertyRequestDTO;
import com.proyecto.proyectoncapas.dto.response.PropertyResponseDTO;
import com.proyecto.proyectoncapas.entities.Property;
import com.proyecto.proyectoncapas.entities.PropertyRule;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.PropertyRepository;
import com.proyecto.proyectoncapas.repository.PropertyRuleRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.services.property.PropertyService;
import com.proyecto.proyectoncapas.utils.mappers.PropertyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyRuleRepository propertyRuleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PropertyResponseDTO createProperty(PropertyRequestDTO request) {
        User landlord = userRepository.findById(request.getLandlordId())
                .orElseThrow(() -> new ResourceNotFoundException("Landlord not found with ID: " + request.getLandlordId()));

        Property property = Property.builder()
                .landlord(landlord)
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .pricePerNight(request.getPricePerNight())
                .maxGuests(request.getMaxGuests())
                .bedrooms(request.getBedrooms())
                .bathrooms(request.getBathrooms())
                .isAvailable(true)
                .averageRating(BigDecimal.ZERO)
                .build();

        property = propertyRepository.save(property);
        log.info("Property created with ID: {}", property.getId());
        return PropertyMapper.toResponseDTO(property);
    }

    @Override
    @Transactional
    public PropertyResponseDTO updateProperty(Long id, PropertyRequestDTO request) {
        Property property = findPropertyOrThrow(id);

        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setCountry(request.getCountry());
        property.setPricePerNight(request.getPricePerNight());
        property.setMaxGuests(request.getMaxGuests());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());

        property = propertyRepository.save(property);
        log.info("Property updated with ID: {}", id);
        return PropertyMapper.toResponseDTO(property);
    }

    @Override
    public PropertyResponseDTO getPropertyById(Long id) {
        return PropertyMapper.toResponseDTO(findPropertyOrThrow(id));
    }

    @Override
    public List<PropertyResponseDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(PropertyMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<PropertyResponseDTO> getPropertiesByLandlord(Long landlordId) {
        return propertyRepository.findByLandlordId(landlordId).stream()
                .map(PropertyMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<PropertyResponseDTO> getAvailableProperties() {
        return propertyRepository.findByIsAvailableTrue().stream()
                .map(PropertyMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<PropertyResponseDTO> getPropertiesByCity(String city) {
        return propertyRepository.findByCityIgnoreCase(city).stream()
                .map(PropertyMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteProperty(Long id) {
        findPropertyOrThrow(id);
        propertyRepository.deleteById(id);
        log.info("Property deleted with ID: {}", id);
    }

    @Override
    @Transactional
    public PropertyResponseDTO addRule(Long propertyId, String description) {
        Property property = findPropertyOrThrow(propertyId);

        PropertyRule rule = PropertyRule.builder()
                .property(property)
                .description(description)
                .build();

        propertyRuleRepository.save(rule);
        log.info("Rule added to Property ID: {}", propertyId);
        return PropertyMapper.toResponseDTO(propertyRepository.findById(propertyId).orElseThrow());
    }

    @Override
    @Transactional
    public void deleteRule(Long ruleId) {
        propertyRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with ID: " + ruleId));
        propertyRuleRepository.deleteById(ruleId);
        log.info("Rule deleted with ID: {}", ruleId);
    }

    private Property findPropertyOrThrow(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + id));
    }
}
