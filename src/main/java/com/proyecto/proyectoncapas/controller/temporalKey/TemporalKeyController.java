package com.proyecto.proyectoncapas.controller.temporalKey;


import com.proyecto.proyectoncapas.dto.request.GenerateKeyRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.TemporalKeyResponseDTO;
import com.proyecto.proyectoncapas.services.temporalKey.TemporalKeyService;
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
public class TemporalKeyController {

    private final TemporalKeyService temporalKeyService;

    // Generación manual/administrativa (la automática ocurre via evento al firmar contrato)
    // Regenerar PINs perdidos o casos especiales
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
    public ResponseEntity<GeneralResponse<TemporalKeyResponseDTO>> generateKey(@Valid @RequestBody GenerateKeyRequestDTO generateKeyRequestDTO) {
        TemporalKeyResponseDTO data = temporalKeyService.generateKeyForContract(generateKeyRequestDTO.getContractId());

        GeneralResponse<TemporalKeyResponseDTO> response = GeneralResponse.<TemporalKeyResponseDTO>builder()
                .message("Access PIN generated successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}
