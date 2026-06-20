package com.proyecto.proyectoncapas.services.maintenance;

import com.proyecto.proyectoncapas.dto.request.MaintenanceTicketRequestDTO;
import com.proyecto.proyectoncapas.dto.request.MaintenanceTicketUpdateRequestDTO;
import com.proyecto.proyectoncapas.dto.request.TicketPhotoRequestDTO;
import com.proyecto.proyectoncapas.dto.response.MaintenanceTicketResponseDTO;
import com.proyecto.proyectoncapas.dto.response.TicketPhotoResponseDTO;
import com.proyecto.proyectoncapas.utils.enums.TicketStatus;

import java.util.List;

public interface MaintenanceTicketService {
    MaintenanceTicketResponseDTO createTicket(Long tenantId, MaintenanceTicketRequestDTO request);
    MaintenanceTicketResponseDTO getTicketById(Long ticketId);
    List<MaintenanceTicketResponseDTO> getTicketsByTenant(Long tenantId, TicketStatus status);
    List<MaintenanceTicketResponseDTO> getTicketsByProperty(Long propertyId, TicketStatus status);
    List<MaintenanceTicketResponseDTO> getTicketsByLandlord(Long landlordId, TicketStatus status);
    MaintenanceTicketResponseDTO updateTicket(Long ticketId, MaintenanceTicketUpdateRequestDTO request);
    MaintenanceTicketResponseDTO updateTicketStatus(Long ticketId, TicketStatus status, Long landlordId);
    void deleteTicket(Long ticketId);
    TicketPhotoResponseDTO uploadTicketPhoto(Long ticketId, TicketPhotoRequestDTO request);
}