package com.proyecto.proyectoncapas.services.review.impl;

import com.proyecto.proyectoncapas.dto.request.ReviewRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ReviewResponseDTO;
import com.proyecto.proyectoncapas.entities.Property;
import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.entities.Review;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.PropertyRepository;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.repository.ReviewRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.services.review.ReviewService;
import com.proyecto.proyectoncapas.utils.mappers.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO request) {
        if (reviewRepository.findByReservationIdAndReviewerId(request.getReservationId(), request.getReviewerId()).isPresent()) {
            throw new IllegalStateException("You have already submitted a review for this reservation");
        }

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + request.getPropertyId()));

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + request.getReservationId()));

        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with ID: " + request.getReviewerId()));

        User reviewee = userRepository.findById(request.getRevieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewee not found with ID: " + request.getRevieweeId()));

        Review review = Review.builder()
                .property(property)
                .reservation(reservation)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .reviewType(request.getReviewType())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        log.info("Review created by User ID: {} for User ID: {} on Property ID: {}",
                request.getReviewerId(), request.getRevieweeId(), request.getPropertyId());

        updatePropertyAverageRating(property);

        return ReviewMapper.toResponseDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByProperty(Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResourceNotFoundException("Property not found with ID: " + propertyId);
        }
        return reviewRepository.findByPropertyId(propertyId).stream()
                .map(ReviewMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        return reviewRepository.findByRevieweeId(userId).stream()
                .map(ReviewMapper::toResponseDTO)
                .toList();
    }

    private void updatePropertyAverageRating(Property property) {
        Double average = reviewRepository.calculateAverageRatingForProperty(property.getId());
        if (average != null) {
            property.setAverageRating(BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
            propertyRepository.save(property);
            log.info("Updated average rating for Property ID: {} → {}", property.getId(), property.getAverageRating());
        }
    }
}
