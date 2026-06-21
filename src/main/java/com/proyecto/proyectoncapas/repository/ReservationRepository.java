package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.utils.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByProperty_Id(Long propertyId);
    List<Reservation> findByTenant_IdOrderByCheckInDateDesc(Long tenantId);
    List<Reservation> findByTenantIdAndStatus(Long tenantId, ReservationStatus status);
}