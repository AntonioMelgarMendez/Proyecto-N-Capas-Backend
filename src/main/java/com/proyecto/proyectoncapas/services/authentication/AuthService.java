package com.proyecto.proyectoncapas.services.authentication;

import com.proyecto.proyectoncapas.dto.request.RegisterRequestDTO;
import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;
import org.springframework.stereotype.Service;

public interface AuthService {
    UserResponseDTO register(RegisterRequestDTO registerRequestDTO);
}
