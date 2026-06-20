package com.proyecto.proyectoncapas.configuration;

import com.proyecto.proyectoncapas.entities.Role;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.repository.RoleRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.utils.enums.RolesName;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Hashear la contraseña para todos los usuarios semilla, para pruebas
    private static final String SEED_PASSWORD = "pass12345";

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Verificando datos semilla (roles y usuarios de prueba)...");

        Role adminRole = seedRole(RolesName.ADMIN, "Adminstrador del sistema, acceso total");
        Role arrendadorRole = seedRole(RolesName.ARRENDADOR,  "Propietario que maneja las propiedades y gestiona contratos");
        Role inquilinoRole = seedRole(RolesName.INQUILINO, "Usuario que reserva y habita las propiedades");

        seedUser(
                "Admin General",
                "admin@rentahome.com",
                "70000000",
                adminRole
        );

        seedUser(
                "Juan Arrendedor",
                "arrendador@rentahome.com",
                "70000001",
                arrendadorRole
        );

        seedUser(
                "Juana Arrendedora",
                "arrendador2@rentahome.com",
                "70000002",
                arrendadorRole
        );

        seedUser(
                "Carlos Inquilino",
                "inquilino@rentahome.com",
                "70000003",
                inquilinoRole
        );

        seedUser(
                "Carla Inquilino",
                "inquilino2@rentahome.com",
                "70000004",
                inquilinoRole
        );
    }

    private Role seedRole(RolesName roleName, String description) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .roleName(roleName)
                            .description(description)
                            .build();
                    Role savedRole = roleRepository.save(role);
                    log.info("Rol creado: {}", roleName);
                    return savedRole;
                });
    }

    private void seedUser(String fullName, String email, String phone, Role role) {
        if (userRepository.findByEmail(email).isPresent()) return;

        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .passwordHash(passwordEncoder.encode(SEED_PASSWORD))
                .phone(phone)
                .isActive(true)
                .isVerified(true)
                .role(role)
                .build();

        userRepository.save(user);
        log.info("Usuario de prueba creado: {} ({})", email, role.getRoleName());
    }

}
