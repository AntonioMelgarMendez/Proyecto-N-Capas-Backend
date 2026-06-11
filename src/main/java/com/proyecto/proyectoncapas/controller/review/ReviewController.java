package com.proyecto.proyectoncapas.controller.review;

import com.proyecto.proyectoncapas.dto.request.ReviewRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.ReviewResponseDTO;
import com.proyecto.proyectoncapas.services.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<GeneralResponse<ReviewResponseDTO>> createReview(
            @Valid @RequestBody ReviewRequestDTO request) {
        ReviewResponseDTO data = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.<ReviewResponseDTO>builder()
                        .message("Review submitted successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<GeneralResponse<List<ReviewResponseDTO>>> getReviewsByProperty(
            @PathVariable Long propertyId) {
        List<ReviewResponseDTO> data = reviewService.getReviewsByProperty(propertyId);
        return ResponseEntity.ok(
                GeneralResponse.<List<ReviewResponseDTO>>builder()
                        .message("Reviews retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse<List<ReviewResponseDTO>>> getReviewsByUser(
            @PathVariable Long userId) {
        List<ReviewResponseDTO> data = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(
                GeneralResponse.<List<ReviewResponseDTO>>builder()
                        .message("Reviews retrieved successfully")
                        .data(data)
                        .build()
        );
    }
}
