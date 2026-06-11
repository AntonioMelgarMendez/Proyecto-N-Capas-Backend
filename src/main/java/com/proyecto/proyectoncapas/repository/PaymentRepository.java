package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findFirstByReservationIdAndStatusOrderByCreatedAtDesc(Long reservationId, String status);
    List<Payment> findByReservationIdOrderByCreatedAtDesc(Long reservationId);
}