package com.proyecto.proyectoncapas.controller.contracts;

import com.proyecto.proyectoncapas.dto.request.SignContractRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ContractResponseDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.services.contract.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @PostMapping("/sign/{reservationId}")
    public ResponseEntity<GeneralResponse<ContractResponseDTO>> signDigitalContract(
            @PathVariable Long reservationId,
            @Valid @RequestBody SignContractRequestDTO request) {
        ContractResponseDTO data = contractService.signContract(reservationId, request);
        GeneralResponse<ContractResponseDTO> response = GeneralResponse.<ContractResponseDTO>builder()
                .message("Contract signed successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<GeneralResponse<ContractResponseDTO>> getContractStatus(@PathVariable Long reservationId) {

        ContractResponseDTO data = contractService.getContractByReservationId(reservationId);

        GeneralResponse<ContractResponseDTO> response = GeneralResponse.<ContractResponseDTO>builder()
                .message("Contract retrieved successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}