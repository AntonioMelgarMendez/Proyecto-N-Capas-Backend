package com.proyecto.proyectoncapas.services.review;

import com.proyecto.proyectoncapas.dto.request.ReviewRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {

    ReviewResponseDTO createReview(ReviewRequestDTO request);

    List<ReviewResponseDTO> getReviewsByProperty(Long propertyId);

    List<ReviewResponseDTO> getReviewsByUser(Long userId);
}
