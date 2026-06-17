package com.proyecto.proyectoncapas.controller.authentication;

import com.proyecto.proyectoncapas.dto.request.LoginRequestDTO;
import com.proyecto.proyectoncapas.dto.request.RegisterRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.JwtResponseDTO;
import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;
import com.proyecto.proyectoncapas.exception.InvalidCredentialsException;
import com.proyecto.proyectoncapas.exception.InvalidPrincipalException;
import com.proyecto.proyectoncapas.exception.MissingRoleException;
import com.proyecto.proyectoncapas.services.authentication.AuthService;
import com.proyecto.proyectoncapas.utils.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<JwtResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        // Autenticar
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Email o contraseña incorrectos");
        }

        // Validar que el principal sea del tipo correcto y no nulo
        Object principal = auth.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            throw new InvalidPrincipalException("El principal no es una instancia válida de UserDetails");
        }

        // Validar que tenga exactamente un rol asignado
        String role = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new MissingRoleException("El usuario no tiene un rol asignado"));

        // Generar el token solo si esta validado
        String token = jwtUtils.generateToken(userDetails);

        // Creamos objeto jwt para generar el generalReponse
        JwtResponseDTO data = new JwtResponseDTO(
                token,
                "Bearer",
                userDetails.getUsername(),
                role
        );

        GeneralResponse<JwtResponseDTO> response = GeneralResponse.<JwtResponseDTO>builder()
                .message("Login successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<UserResponseDTO>> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        UserResponseDTO userCreated = authService.register(registerRequestDTO);
        GeneralResponse<UserResponseDTO> response = GeneralResponse.<UserResponseDTO>builder()
                .message("User created successfully")
                .data(userCreated)
                .build();

        return ResponseEntity.ok(response);
    }
}
