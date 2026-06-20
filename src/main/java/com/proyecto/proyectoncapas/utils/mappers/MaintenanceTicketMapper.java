package com.proyecto.proyectoncapas.utils.mappers;

import com.proyecto.proyectoncapas.dto.response.MaintenanceTicketResponseDTO;
import com.proyecto.proyectoncapas.dto.response.TicketPhotoResponseDTO;
import com.proyecto.proyectoncapas.entities.MaintenanceTicket;
import com.proyecto.proyectoncapas.entities.TicketPhoto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceTicketMapper {

    public static MaintenanceTicketResponseDTO toDTO(MaintenanceTicket ticket) {
        List<TicketPhoto> rawPhotos = ticket.getPhotos() != null ? ticket.getPhotos() : Collections.emptyList();
        List<TicketPhotoResponseDTO> photos = rawPhotos.stream()
                .map(photo -> TicketPhotoResponseDTO.builder()
                        .id(photo.getId())
                        .photoUrl(photo.getPhotoUrl())
                        .s3Key(photo.getS3Key())
                        .build())
                .collect(Collectors.toList());

        Long landlordId = ticket.getLandlord() != null ? ticket.getLandlord().getId() : null;
        String landlordName = ticket.getLandlord() != null ? ticket.getLandlord().getFullName() : null;

        return MaintenanceTicketResponseDTO.builder()
                .id(ticket.getId())
                .propertyId(ticket.getProperty().getId())
                .propertyTitle(ticket.getProperty().getTitle())
                .tenantId(ticket.getTenant().getId())
                .tenantName(ticket.getTenant().getFullName())
                .landlordId(landlordId)
                .landlordName(landlordName)
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .photos(photos)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }
}
