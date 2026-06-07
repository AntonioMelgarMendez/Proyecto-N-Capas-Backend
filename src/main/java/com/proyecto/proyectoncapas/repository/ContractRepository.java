package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByReservationId(Long reservationId);
}