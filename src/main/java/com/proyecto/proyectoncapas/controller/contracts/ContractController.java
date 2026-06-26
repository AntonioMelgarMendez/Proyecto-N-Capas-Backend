package com.proyecto.proyectoncapas.controller.contracts;

import com.proyecto.proyectoncapas.dto.request.SignContractRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ContractResponseDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.services.contract.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contracts", description = "Digital contract signing and retrieval for reservations")
public class ContractController {

    private final ContractService contractService;

    @PostMapping("/sign/{reservationId}")
    @Operation(summary = "Sign digital contract", description = "Tenant digitally signs the rental contract for a confirmed reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract signed successfully"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "409", description = "Contract already signed")
    })
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
    @Operation(summary = "Get contract by reservation", description = "Retrieve the contract and its signature status for a given reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Contract not found for this reservation")
    })
    public ResponseEntity<GeneralResponse<ContractResponseDTO>> getContractStatus(@PathVariable Long reservationId) {

        ContractResponseDTO data = contractService.getContractByReservationId(reservationId);

        GeneralResponse<ContractResponseDTO> response = GeneralResponse.<ContractResponseDTO>builder()
                .message("Contract retrieved successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}
