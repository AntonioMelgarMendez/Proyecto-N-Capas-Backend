package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.proyectoncapas.utils.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByProperty_Id(Long propertyId);
    List<Reservation> findByTenant_IdOrderByCheckInDateDesc(Long tenantId);
    List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime createdAt);
}