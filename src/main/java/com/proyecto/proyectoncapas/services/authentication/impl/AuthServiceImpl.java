package com.proyecto.proyectoncapas.services.authentication.impl;

import com.proyecto.proyectoncapas.dto.request.RegisterRequestDTO;
import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;
import com.proyecto.proyectoncapas.entities.Role;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.RoleNotFoundException;
import com.proyecto.proyectoncapas.exception.UserAlreadyExistsException;
import com.proyecto.proyectoncapas.repository.RoleRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.services.authentication.AuthService;
import com.proyecto.proyectoncapas.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException("El email ya existe");
        }

        Role role = roleRepository.findById(registerRequestDTO.getRoleID())
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));

        User user = UserMapper.toEntity(registerRequestDTO, role, passwordEncoder);
        User savedUser = userRepository.save(user);

        return UserMapper.userResponseDTO(savedUser);
    }
}
