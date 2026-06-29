package com.proyecto.proyectoncapas.utils.mappers;

import com.proyecto.proyectoncapas.dto.request.RegisterRequestDTO;
import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;
import com.proyecto.proyectoncapas.entities.Role;
import com.proyecto.proyectoncapas.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class UserMapper {
    public static UserResponseDTO userResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .active(user.getIsActive())
                .verified(user.getIsVerified())
                .roleName(user.getRole().getRoleName().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static User toEntity(RegisterRequestDTO registerRequestDTO, Role role, PasswordEncoder passwordEncoder) {
        return User.builder()
                .fullName(registerRequestDTO.getFullname())
                .email(registerRequestDTO.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .phone(registerRequestDTO.getPhone())
                .isActive(true)
                .legacyRole(role.getRoleName().name())
                .role(role)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
