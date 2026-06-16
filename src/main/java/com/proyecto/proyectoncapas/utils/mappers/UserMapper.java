package com.proyecto.proyectoncapas.utils.mappers;

import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;
import com.proyecto.proyectoncapas.entities.User;

public class UserMapper {
    public UserResponseDTO userResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .active(user.getIsActive())
                .roleName(user.getRole().getRoleName().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
