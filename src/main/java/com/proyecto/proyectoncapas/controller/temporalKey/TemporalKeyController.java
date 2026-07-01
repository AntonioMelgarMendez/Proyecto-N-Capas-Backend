package com.proyecto.proyectoncapas.controller.temporalKey;


import com.proyecto.proyectoncapas.dto.request.GenerateKeyRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.TemporalKeyResponseDTO;
import com.proyecto.proyectoncapas.services.temporalKey.TemporalKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
@Tag(name = "Temporal Keys / Access PINs", description = "Endpoints for generating and managing temporal smart-lock access PINs for active contracts")
public class TemporalKeyController {

    private final TemporalKeyService temporalKeyService;

    // Generación manual/administrativa (la automática ocurre via evento al firmar contrato)
    // Regenerar PINs perdidos o casos especiales
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
    @Operation(summary = "Generate manual access PIN", description = "Manually generate/regenerate a temporary smart-lock PIN for a contract. (Automatic generation occurs during signing). Accessible by Admin and Landlords")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access PIN successfully generated"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or contract is not active"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    public ResponseEntity<GeneralResponse<TemporalKeyResponseDTO>> generateKey(@Valid @RequestBody GenerateKeyRequestDTO generateKeyRequestDTO) {
        TemporalKeyResponseDTO data = temporalKeyService.generateKeyForContract(generateKeyRequestDTO.getContractId());

        GeneralResponse<TemporalKeyResponseDTO> response = GeneralResponse.<TemporalKeyResponseDTO>builder()
                .message("Access PIN generated successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}
