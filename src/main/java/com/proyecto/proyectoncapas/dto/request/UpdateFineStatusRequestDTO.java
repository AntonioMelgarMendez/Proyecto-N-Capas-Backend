package com.proyecto.proyectoncapas.dto.request;

import com.proyecto.proyectoncapas.utils.enums.FineStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFineStatusRequestDTO {
    @NotNull(message = "New fine status is mandatory")
    private FineStatus fineStatus;
}
