package com.proyecto.proyectoncapas.utils.mappers;

import com.proyecto.proyectoncapas.dto.response.MaintenanceTicketResponseDTO;
import com.proyecto.proyectoncapas.dto.response.TicketPhotoResponseDTO;
import com.proyecto.proyectoncapas.entities.MaintenanceTicket;

import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceTicketMapper {

    public static MaintenanceTicketResponseDTO toDTO(MaintenanceTicket ticket) {
        List<TicketPhotoResponseDTO> photos = ticket.getPhotos().stream()
                .map(photo -> TicketPhotoResponseDTO.builder()
                        .id(photo.getId())
                        .photoUrl(photo.getPhotoUrl())
                        .s3Key(photo.getS3Key())
                        .build())
                .collect(Collectors.toList());

        return MaintenanceTicketResponseDTO.builder()
                .id(ticket.getId())
                .propertyId(ticket.getProperty().getId())
                .propertyTitle(ticket.getProperty().getTitle())
                .tenantId(ticket.getTenant().getId())
                .tenantName(ticket.getTenant().getFullName())
                .landlordId(ticket.getLandlord().getId())
                .landlordName(ticket.getLandlord().getFullName())
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
