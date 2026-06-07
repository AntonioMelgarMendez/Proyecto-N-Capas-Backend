package com.proyecto.proyectoncapas.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponseDTO {

    private Long id;
    private Long landlordId;
    private String landlordName;
    private String title;
    private String description;
    private String address;
    private String city;
    private String country;
    private BigDecimal pricePerNight;
    private Integer maxGuests;
    private Integer bedrooms;
    private Integer bathrooms;
    private Boolean isAvailable;
    private BigDecimal averageRating;
    private LocalDateTime createdAt;
}
