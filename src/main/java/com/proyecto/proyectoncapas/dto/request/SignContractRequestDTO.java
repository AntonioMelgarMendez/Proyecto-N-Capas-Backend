package com.proyecto.proyectoncapas.dto.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class SignContractRequestDTO {
    @AssertTrue(message = "You must accept the terms and conditions to sign the contract")
    private boolean termsAccepted;

    private String ipAddress;
}