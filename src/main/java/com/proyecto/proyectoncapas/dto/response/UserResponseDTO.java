package com.proyecto.proyectoncapas.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Boolean active;
    private Boolean verified;
    private String roleName;
    private LocalDateTime createdAt;
}
