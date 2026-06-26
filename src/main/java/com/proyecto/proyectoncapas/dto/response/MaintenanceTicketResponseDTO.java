package com.proyecto.proyectoncapas.dto.response;

import com.proyecto.proyectoncapas.utils.enums.TicketPriority;
import com.proyecto.proyectoncapas.utils.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTicketResponseDTO {
    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private Long tenantId;
    private String tenantName;
    private Long landlordId;
    private String landlordName;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private List<TicketPhotoResponseDTO> photos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
}