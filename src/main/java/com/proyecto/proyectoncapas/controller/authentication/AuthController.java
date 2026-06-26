package com.proyecto.proyectoncapas.controller.authentication;

import com.proyecto.proyectoncapas.dto.request.LoginRequestDTO;
import com.proyecto.proyectoncapas.dto.request.RegisterRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.LoginResponseDTO;
import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;
import com.proyecto.proyectoncapas.services.authentication.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO data = authService.login(loginRequestDTO);

        GeneralResponse<LoginResponseDTO> response = GeneralResponse.<LoginResponseDTO>builder()
                .message("Login successful!")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<UserResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        UserResponseDTO data = authService.register(registerRequestDTO);

        GeneralResponse<UserResponseDTO> response = GeneralResponse.<UserResponseDTO>builder()
                .message("User registered successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

}