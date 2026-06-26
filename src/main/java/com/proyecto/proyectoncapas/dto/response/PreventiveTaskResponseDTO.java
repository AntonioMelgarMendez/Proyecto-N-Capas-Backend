package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreventiveTaskResponseDTO {
    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private String title;
    private String description;
    private LocalDate scheduledDate;
    private String status;
    private LocalDateTime createdAt;
}
