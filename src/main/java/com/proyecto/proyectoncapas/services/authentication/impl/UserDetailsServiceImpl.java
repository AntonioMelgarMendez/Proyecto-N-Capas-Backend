package com.proyecto.proyectoncapas.services.authentication.impl;

import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.DisabledUserException;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.services.authentication.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado" + email));
        if (!user.getIsActive()) {
            throw new DisabledUserException("Usuario desactivado");
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(authority)
        );
    }

}
