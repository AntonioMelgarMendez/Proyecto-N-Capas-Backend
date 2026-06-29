package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.ExtensionRequest;
import com.proyecto.proyectoncapas.utils.enums.ExtensionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtensionRequestRepository extends JpaRepository<ExtensionRequest, Long> {

    List<ExtensionRequest> findByReservationIdOrderByRequestedAtDesc(Long reservationId);

    Optional<ExtensionRequest> findByIdAndStatus(Long id, ExtensionRequestStatus status);

    boolean existsByReservationIdAndStatusIn(Long reservationId, List<ExtensionRequestStatus> statuses);

    @Query("""
            SELECT e FROM ExtensionRequest e
            WHERE e.reservation.property.landlord.id = :landlordId
            AND (:status IS NULL OR e.status = :status)
            ORDER BY e.requestedAt DESC
            """)
    List<ExtensionRequest> findByLandlordIdAndOptionalStatus(
            @Param("landlordId") Long landlordId,
            @Param("status") ExtensionRequestStatus status);
}
