package com.proyecto.proyectoncapas.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateKeyRequestDTO {
    @NotNull(message = "Contract is mandatory")
    private Long contractId;
}
