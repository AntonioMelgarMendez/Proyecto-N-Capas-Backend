// TicketPhotoRequestDTO.java
package com.proyecto.proyectoncapas.dto.request;

import lombok.Data;

@Data
public class TicketPhotoRequestDTO {
    private String photoUrl;
    private String s3Key;
}