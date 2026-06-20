package com.proyecto.proyectoncapas.services.authentication.impl;

import com.proyecto.proyectoncapas.dto.request.LoginRequestDTO;
import com.proyecto.proyectoncapas.dto.request.RegisterRequestDTO;
import com.proyecto.proyectoncapas.dto.response.LoginResponseDTO;
import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;
import com.proyecto.proyectoncapas.entities.Role;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.EmailAlreadyExistsException;
import com.proyecto.proyectoncapas.exception.InvalidCredentialsException;
import com.proyecto.proyectoncapas.exception.RoleNotFoundException;
import com.proyecto.proyectoncapas.repository.RoleRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.services.authentication.AuthService;
import com.proyecto.proyectoncapas.utils.enums.RolesName;
import com.proyecto.proyectoncapas.utils.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder  passwordEncoder;
    private final JwtUtils jwtUtil;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

            String roleName = user.getRole().getRoleName().name();
            String token = jwtUtil.generateToken(userDetails, user.getId(),  roleName);

            return LoginResponseDTO.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .role(roleName)
                    .build();

        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Email o contraseña incorrecta");
        }
    }

    @Override
    @Transactional
    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {

        if (userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "Ya existe una cuenta registrada con este email: " + registerRequestDTO.getEmail());
        }

        RolesName requestedRole = resolverRequestedRole(registerRequestDTO.getRoleName());

        Role role = roleRepository.findByRoleName(requestedRole)
                .orElseThrow(() -> new RoleNotFoundException(
                        "Rol no configurado en el sistema: " + requestedRole));

        User user = User.builder()
                .fullName(registerRequestDTO.getFullname())
                .email(registerRequestDTO.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .phone(registerRequestDTO.getPhone())
                .isActive(true)
                .isVerified(false)
                .role(role)
                .build();

        User saved = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .phoneNumber(saved.getPhone())
                .roleName(role.getRoleName().name())
                .active(saved.getIsActive())
                .verified(saved.getIsVerified())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    private RolesName resolverRequestedRole(String roleName) {
        // Para dar un rol de usuario por defecto inquilino (el de menos permisos)
        if (roleName == null || roleName.isBlank()) return RolesName.INQUILINO;

        RolesName parsed;
        try {
            parsed = RolesName.valueOf(roleName.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RoleNotFoundException("Rol inválido: " + roleName);
        }

        // Admins no pueden ser creados por el sistema, solo internamente
        if (parsed == RolesName.ADMIN) {
            throw new RoleNotFoundException("No es posible auto-registrarse con el rol ADMIN");
        }

        return parsed;
    }

}
