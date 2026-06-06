package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByStripeSessionId(String stripeSessionId);
}