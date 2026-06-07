package com.proyecto.proyectoncapas.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignContractRequestDTO {
    @NotBlank(message = "IP Address cannot be empty")
    private String ipAddress;

    @AssertTrue(message = "You must accept the terms and conditions")
    private Boolean termsAccepted;
}