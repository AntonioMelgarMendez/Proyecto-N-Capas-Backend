package com.proyecto.proyectoncapas.utils.mappers;

import com.proyecto.proyectoncapas.dto.response.PropertyResponseDTO;
import com.proyecto.proyectoncapas.entities.Property;

public class PropertyMapper {

    public static PropertyResponseDTO toResponseDTO(Property property) {
        return PropertyResponseDTO.builder()
                .id(property.getId())
                .landlordId(property.getLandlord().getId())
                .landlordName(property.getLandlord().getFullName())
                .title(property.getTitle())
                .description(property.getDescription())
                .address(property.getAddress())
                .city(property.getCity())
                .country(property.getCountry())
                .pricePerNight(property.getPricePerNight())
                .maxGuests(property.getMaxGuests())
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .isAvailable(property.getIsAvailable())
                .averageRating(property.getAverageRating())
                .createdAt(property.getCreatedAt())
                .build();
    }
}
