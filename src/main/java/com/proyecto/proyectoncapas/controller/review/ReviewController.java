package com.proyecto.proyectoncapas.controller.review;

import com.proyecto.proyectoncapas.dto.request.ReviewRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.ReviewResponseDTO;
import com.proyecto.proyectoncapas.services.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Create and retrieve reviews between tenants and landlords")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Submit review", description = "Submit a review from tenant to landlord or landlord to tenant after a completed reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review submitted"),
            @ApiResponse(responseCode = "409", description = "Review already submitted for this reservation"),
            @ApiResponse(responseCode = "404", description = "Property, reservation, or user not found")
    })
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
    @Operation(summary = "Get reviews by property", description = "Retrieve all reviews for a specific property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews returned"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
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
    @Operation(summary = "Get reviews by user", description = "Retrieve all reviews received by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews returned"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
