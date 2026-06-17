package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.MaintenanceTicket;
import com.proyecto.proyectoncapas.utils.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceTicketRepository extends JpaRepository<MaintenanceTicket, Long> {

    List<MaintenanceTicket> findByPropertyId(Long propertyId);
    List<MaintenanceTicket> findByTenantId(Long tenantId);
    List<MaintenanceTicket> findByLandlordId(Long landlordId);
    List<MaintenanceTicket> findByStatus(TicketStatus status);
    List<MaintenanceTicket> findByPropertyIdAndStatus(Long propertyId, TicketStatus status);

    @Query(value = """
        SELECT
            p.id as property_id,
            p.title as property_title,
            COUNT(*) as total_tickets,
            SUM(CASE WHEN mt.status = 'OPEN' THEN 1 ELSE 0 END) as open_tickets,
            SUM(CASE WHEN mt.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress_tickets,
            SUM(CASE WHEN mt.status = 'RESOLVED' THEN 1 ELSE 0 END) as resolved_tickets
        FROM maintenance_tickets mt
        JOIN properties p ON mt.property_id = p.id
        WHERE mt.created_at BETWEEN :startDate AND :endDate
          AND p.landlord_id = :landlordId
        GROUP BY p.id, p.title
        """, nativeQuery = true)
    List<Object[]> getMaintenanceMetricsByProperty(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("landlordId") Long landlordId
    );
}