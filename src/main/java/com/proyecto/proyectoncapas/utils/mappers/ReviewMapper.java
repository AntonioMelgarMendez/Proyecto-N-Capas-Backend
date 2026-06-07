package com.proyecto.proyectoncapas.utils.mappers;

import com.proyecto.proyectoncapas.dto.response.ReviewResponseDTO;
import com.proyecto.proyectoncapas.entities.Review;

public class ReviewMapper {

    public static ReviewResponseDTO toResponseDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .propertyId(review.getProperty().getId())
                .reservationId(review.getReservation().getId())
                .reviewerId(review.getReviewer().getId())
                .reviewerName(review.getReviewer().getFullName())
                .revieweeId(review.getReviewee().getId())
                .revieweeName(review.getReviewee().getFullName())
                .reviewType(review.getReviewType())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
