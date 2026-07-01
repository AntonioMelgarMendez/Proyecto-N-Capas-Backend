package com.proyecto.proyectoncapas.controller.authentication;

import com.proyecto.proyectoncapas.dto.request.LoginRequestDTO;
import com.proyecto.proyectoncapas.dto.request.RegisterRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.LoginResponseDTO;
import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;
import com.proyecto.proyectoncapas.services.authentication.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate a user with email and password to retrieve a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<GeneralResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO data = authService.login(loginRequestDTO);

        GeneralResponse<LoginResponseDTO> response = GeneralResponse.<LoginResponseDTO>builder()
                .message("Login successful!")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new tenant or landlord user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<GeneralResponse<UserResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        UserResponseDTO data = authService.register(registerRequestDTO);

        GeneralResponse<UserResponseDTO> response = GeneralResponse.<UserResponseDTO>builder()
                .message("User registered successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

}