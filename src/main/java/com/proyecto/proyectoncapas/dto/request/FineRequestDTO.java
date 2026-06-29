package com.proyecto.proyectoncapas.dto.request;

import com.proyecto.proyectoncapas.utils.enums.InfractionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineRequestDTO {
    @NotNull(message = "Infractor user is mandatory")
    private Long userId;
    @NotNull(message = "Infraction type is mandatory")
    private InfractionType infractionType;
    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    @NotBlank(message = "Description is mandatory")
    private String description;

    private LocalDateTime infractionDate;
}
