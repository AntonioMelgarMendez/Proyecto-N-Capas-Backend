package com.proyecto.proyectoncapas.utils.security;

import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.InvalidPrincipalException;
import com.proyecto.proyectoncapas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new InvalidPrincipalException("No hay un usuario autenticado válido en el contexto");
        }

        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new InvalidPrincipalException(
                        "Usuario autenticado no encontrado: " + userDetails.getUsername()));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
