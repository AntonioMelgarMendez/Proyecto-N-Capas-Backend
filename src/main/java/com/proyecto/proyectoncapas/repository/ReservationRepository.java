package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.proyectoncapas.utils.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByProperty_Id(Long propertyId);
    List<Reservation> findByTenant_IdOrderByCheckInDateDesc(Long tenantId);
    List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime createdAt);

    // Occupancy Metrics Queries
    @Query(value = """
            SELECT 
                p.id as property_id,
                p.title as property_title,
                COUNT(r.id) as total_reservations,
                COALESCE(SUM(p.daily_price * EXTRACT(DAY FROM (r.check_out_date - r.check_in_date))), 0) as total_revenue,
                SUM(EXTRACT(DAY FROM (r.check_out_date - r.check_in_date))) as total_days_occupied
            FROM reservations r
            JOIN properties p ON r.property_id = p.id
            WHERE p.user_id = :landlordId
                AND r.status IN ('CONFIRMED', 'COMPLETED', 'EXTENDED')
                AND r.check_in_date >= :startDate
                AND r.check_out_date <= :endDate
            GROUP BY p.id, p.title
            """, nativeQuery = true)
    List<Object[]> getOccupancyMetricsByLandlord(
            @Param("landlordId") Long landlordId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
            SELECT 
                p.id as property_id,
                p.title as property_title,
                COUNT(r.id) as total_reservations,
                COALESCE(SUM(p.daily_price * EXTRACT(DAY FROM (r.check_out_date - r.check_in_date))), 0) as total_revenue,
                SUM(EXTRACT(DAY FROM (r.check_out_date - r.check_in_date))) as total_days_occupied
            FROM reservations r
            JOIN properties p ON r.property_id = p.id
            WHERE p.id = :propertyId
                AND r.status IN ('CONFIRMED', 'COMPLETED', 'EXTENDED')
                AND r.check_in_date >= :startDate
                AND r.check_out_date <= :endDate
            GROUP BY p.id, p.title
            """, nativeQuery = true)
    List<Object[]> getOccupancyMetricsByProperty(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}