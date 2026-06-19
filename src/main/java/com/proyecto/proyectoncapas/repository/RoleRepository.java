package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Role;
import com.proyecto.proyectoncapas.utils.enums.RolesName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(RolesName roleName);
}
