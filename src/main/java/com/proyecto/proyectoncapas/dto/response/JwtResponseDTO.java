package com.proyecto.proyectoncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String type;
    private String email;
    private String role;
}
