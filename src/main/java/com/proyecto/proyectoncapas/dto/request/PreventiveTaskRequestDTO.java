package com.proyecto.proyectoncapas.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreventiveTaskRequestDTO {

    @NotNull(message = "La propiedad es requerida")
    private Long propertyId;

    @NotBlank(message = "El título es requerido")
    @Size(max = 120, message = "El título no puede superar 120 caracteres")
    private String title;

    @Size(max = 500)
    private String description;

    @NotNull(message = "La fecha programada es requerida")
    @FutureOrPresent(message = "La fecha no puede ser en el pasado")
    private LocalDate scheduledDate;
}
