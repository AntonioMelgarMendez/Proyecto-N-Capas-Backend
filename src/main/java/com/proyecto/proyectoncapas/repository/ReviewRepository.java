package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPropertyId(Long propertyId);

    List<Review> findByRevieweeId(Long revieweeId);

    List<Review> findByReviewerId(Long reviewerId);

    Optional<Review> findByReservationIdAndReviewerId(Long reservationId, Long reviewerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :userId AND r.reviewType = :reviewType")
    Double calculateAverageRatingForUser(@Param("userId") Long userId, @Param("reviewType") String reviewType);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propertyId")
    Double calculateAverageRatingForProperty(@Param("propertyId") Long propertyId);
}
