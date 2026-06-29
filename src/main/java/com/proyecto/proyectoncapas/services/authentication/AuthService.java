package com.proyecto.proyectoncapas.services.authentication;

import com.proyecto.proyectoncapas.dto.request.LoginRequestDTO;
import com.proyecto.proyectoncapas.dto.request.RegisterRequestDTO;
import com.proyecto.proyectoncapas.dto.response.LoginResponseDTO;
import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
