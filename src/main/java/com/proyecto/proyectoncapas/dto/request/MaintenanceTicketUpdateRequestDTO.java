package com.proyecto.proyectoncapas.dto.request;

import com.proyecto.proyectoncapas.utils.enums.TicketPriority;
import lombok.Data;

@Data
public class MaintenanceTicketUpdateRequestDTO {

    private String title;

    private String description;

    private TicketPriority priority;
}
