// MaintenanceTicketServiceImpl.java
package com.proyecto.proyectoncapas.services.maintenance.impl;

import com.proyecto.proyectoncapas.dto.request.MaintenanceTicketRequestDTO;
import com.proyecto.proyectoncapas.dto.response.MaintenanceTicketResponseDTO;
import com.proyecto.proyectoncapas.dto.response.TicketPhotoResponseDTO;
import com.proyecto.proyectoncapas.entities.MaintenanceTicket;
import com.proyecto.proyectoncapas.entities.Property;
import com.proyecto.proyectoncapas.entities.TicketPhoto;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.MaintenanceTicketRepository;
import com.proyecto.proyectoncapas.repository.PropertyRepository;
import com.proyecto.proyectoncapas.repository.TicketPhotoRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.services.maintenance.MaintenanceTicketService;
import com.proyecto.proyectoncapas.services.s3.S3Service;
import com.proyecto.proyectoncapas.utils.enums.TicketStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MaintenanceTicketServiceImpl implements MaintenanceTicketService {

    private final MaintenanceTicketRepository ticketRepository;
    private final TicketPhotoRepository photoRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Override
    public MaintenanceTicketResponseDTO createTicket(Long tenantId, MaintenanceTicketRequestDTO request, List<MultipartFile> photos) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        MaintenanceTicket ticket = MaintenanceTicket.builder()
                .property(property)
                .tenant(tenant)
                .landlord(property.getLandlord())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .build();

        MaintenanceTicket savedTicket = ticketRepository.save(ticket);

        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                String s3Key = s3Service.uploadFile(photo, Long.valueOf("maintenance-tickets/" + savedTicket.getId()));
                TicketPhoto ticketPhoto = TicketPhoto.builder()
                        .ticket(savedTicket)
                        .photoUrl(s3Service.getFileUrl(s3Key))
                        .s3Key(s3Key)
                        .build();
                photoRepository.save(ticketPhoto);
            }
        }

        return mapToResponseDTO(savedTicket);
    }

    @Override
    public MaintenanceTicketResponseDTO getTicketById(Long ticketId) {
        MaintenanceTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return mapToResponseDTO(ticket);
    }

    @Override
    public List<MaintenanceTicketResponseDTO> getTicketsByTenant(Long tenantId) {
        return ticketRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceTicketResponseDTO> getTicketsByProperty(Long propertyId) {
        return ticketRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceTicketResponseDTO> getTicketsByLandlord(Long landlordId) {
        return ticketRepository.findByLandlordId(landlordId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceTicketResponseDTO updateTicketStatus(Long ticketId, TicketStatus status, Long landlordId) {
        MaintenanceTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (!ticket.getLandlord().getId().equals(landlordId)) {
            throw new SecurityException("Only the property landlord can update ticket status");
        }

        ticket.setStatus(status);
        if (status == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        MaintenanceTicket updated = ticketRepository.save(ticket);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteTicket(Long ticketId) {
        MaintenanceTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Eliminar fotos de S3
        ticket.getPhotos().forEach(photo -> s3Service.deleteFile(photo.getS3Key()));

        ticketRepository.delete(ticket);
    }

    private MaintenanceTicketResponseDTO mapToResponseDTO(MaintenanceTicket ticket) {
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