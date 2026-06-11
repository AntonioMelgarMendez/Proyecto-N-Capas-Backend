package com.proyecto.proyectoncapas.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotNull(message = "Reviewer ID is required")
    private Long reviewerId;

    @NotNull(message = "Reviewee ID is required")
    private Long revieweeId;

    @NotBlank(message = "Review type is required")
    @Pattern(regexp = "TENANT_TO_LANDLORD|LANDLORD_TO_TENANT", message = "Review type must be TENANT_TO_LANDLORD or LANDLORD_TO_TENANT")
    private String reviewType;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    private String comment;
}
