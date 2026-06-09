package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.PropertyAvailability;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PropertyAvailabilityRepository extends JpaRepository<PropertyAvailability, Long> {

    // Para obtener el calendario (Lectura limpia y rápida)
    @Query("SELECT a.date FROM PropertyAvailability a WHERE a.property.id = :propertyId AND a.date BETWEEN :start AND :end")
    List<LocalDate> findOccupiedDates(
            @Param("propertyId") Long propertyId,
            @Param("start") LocalDate start, 
            @Param("end") LocalDate end);

    // CRITICAL: Bloquea las filas para evitar condiciones de carrera al reservar
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(a) FROM PropertyAvailability a WHERE a.property.id = :propertyId AND a.date BETWEEN :start AND :end")
    long countOccupiedDaysForUpdate(
            @Param("propertyId") Long propertyId, 
            @Param("start") LocalDate start, 
            @Param("end") LocalDate end);

    @Modifying
    @Query("DELETE FROM PropertyAvailability a WHERE a.reservation.id = :reservationId")
    void deleteByReservationId(@Param("bookingId") Long reservationId);}