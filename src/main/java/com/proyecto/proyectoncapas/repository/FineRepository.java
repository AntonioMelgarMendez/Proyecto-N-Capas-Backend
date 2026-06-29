package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Fine;
import com.proyecto.proyectoncapas.utils.enums.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByUserId(Long id);
    List<Fine> findByStatus(FineStatus status);
    List<Fine> findByUserIdAndStatus(Long userId, FineStatus status);
}

