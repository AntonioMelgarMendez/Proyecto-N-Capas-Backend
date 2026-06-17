package com.proyecto.proyectoncapas.entities;

import com.proyecto.proyectoncapas.utils.enums.RolesName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name")
    private RolesName roleName;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
