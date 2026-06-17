// MaintenanceTicketService.java
package com.proyecto.proyectoncapas.services.maintenance;

import com.proyecto.proyectoncapas.dto.request.MaintenanceTicketRequestDTO;
import com.proyecto.proyectoncapas.dto.request.TicketPhotoRequestDTO;
import com.proyecto.proyectoncapas.dto.response.MaintenanceTicketResponseDTO;
import com.proyecto.proyectoncapas.dto.response.TicketPhotoResponseDTO;
import com.proyecto.proyectoncapas.utils.enums.TicketStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MaintenanceTicketService {
    MaintenanceTicketResponseDTO createTicket(Long tenantId, MaintenanceTicketRequestDTO request, List<MultipartFile> photos);
    MaintenanceTicketResponseDTO getTicketById(Long ticketId);
    List<MaintenanceTicketResponseDTO> getTicketsByTenant(Long tenantId);
    List<MaintenanceTicketResponseDTO> getTicketsByProperty(Long propertyId);
    List<MaintenanceTicketResponseDTO> getTicketsByLandlord(Long landlordId);
    MaintenanceTicketResponseDTO updateTicketStatus(Long ticketId, TicketStatus status, Long landlordId);
    void deleteTicket(Long ticketId);
    TicketPhotoResponseDTO uploadTicketPhoto(Long ticketId, TicketPhotoRequestDTO request);
}