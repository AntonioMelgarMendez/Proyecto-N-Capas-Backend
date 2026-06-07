package com.proyecto.proyectoncapas.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {

    private Long id;
    private Long propertyId;
    private Long reservationId;
    private Long reviewerId;
    private String reviewerName;
    private Long revieweeId;
    private String revieweeName;
    private String reviewType;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
