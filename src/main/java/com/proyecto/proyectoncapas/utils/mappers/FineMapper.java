package com.proyecto.proyectoncapas.utils.mappers;


import com.proyecto.proyectoncapas.dto.response.FineResponseDTO;
import com.proyecto.proyectoncapas.entities.Fine;

public class FineMapper {

    public static FineResponseDTO toResponseDTO(Fine fine) {
        if (fine == null) return null;

        return FineResponseDTO.builder()
                .fineId(fine.getFineId())
                .userId(fine.getUser().getId())
                .userFullName(fine.getUser().getFullName())
                .generatedById(fine.getGeneratedBy().getId())
                .generatedByFullName(fine.getGeneratedBy().getFullName())
                .infractionType(fine.getInfractionType())
                .status(fine.getStatus())
                .amount(fine.getAmount())
                .description(fine.getDescription())
                .infractionDate(fine.getInfractionDate())
                .creationDate(fine.getCreatedAt())
                .build();
    }
}
