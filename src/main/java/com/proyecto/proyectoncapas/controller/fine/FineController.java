package com.proyecto.proyectoncapas.controller.fine;

import com.proyecto.proyectoncapas.dto.request.FineRequestDTO;
import com.proyecto.proyectoncapas.dto.request.UpdateFineStatusRequestDTO;
import com.proyecto.proyectoncapas.dto.response.FineResponseDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.services.fines.FineService;
import com.proyecto.proyectoncapas.utils.security.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
@Tag(name = "Fines / Violations", description = "Endpoints for creating, managing, and retrieving tenant fines or noise policy violations")
public class FineController {

    private final FineService fineService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
    @Operation(summary = "Create a new fine", description = "Issue a fine to a tenant. Accessible by Admin and Landlords")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fine successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "User or reservation not found")
    })
    public ResponseEntity<GeneralResponse<FineResponseDTO>> createFine(@Valid @RequestBody FineRequestDTO fineRequestDTO) {

        Long generatedByUserId = authenticatedUserProvider.getCurrentUserId();
        FineResponseDTO data = fineService.createFine(fineRequestDTO, generatedByUserId);

        GeneralResponse<FineResponseDTO> response = GeneralResponse.<FineResponseDTO>builder()
                .message("Fine created successfully")
                .data(data)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{fineId}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
    @Operation(summary = "Update fine status", description = "Update the payment or resolution status of a fine. Accessible by Admin and Landlords")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fine status successfully updated"),
            @ApiResponse(responseCode = "404", description = "Fine not found")
    })
    public ResponseEntity<GeneralResponse<FineResponseDTO>> updateFineStatus(@PathVariable Long fineId, @Valid @RequestBody UpdateFineStatusRequestDTO fineUpdateRequestDTO) {

        FineResponseDTO data = fineService.updateFineStatus(fineId, fineUpdateRequestDTO);

        GeneralResponse<FineResponseDTO> response = GeneralResponse.<FineResponseDTO>builder()
                .message("Fine status updated successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
    @Operation(summary = "Get all fines", description = "Retrieve all fines registered in the platform. Accessible by Admin and Landlords")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fines successfully retrieved")
    })
    public ResponseEntity<GeneralResponse<List<FineResponseDTO>>> getAllFines() {

        List<FineResponseDTO> data = fineService.getAllFines();

        GeneralResponse<List<FineResponseDTO>> response = GeneralResponse.<List<FineResponseDTO>>builder()
                .message("Fines retrieved successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's fines", description = "Retrieve list of fines issued to the authenticated tenant user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Your fines retrieved successfully")
    })
    public ResponseEntity<GeneralResponse<List<FineResponseDTO>>> getMyFines() {

        Long userId = authenticatedUserProvider.getCurrentUserId();
        List<FineResponseDTO> data = fineService.getFineByUser(userId);

        GeneralResponse<List<FineResponseDTO>> response = GeneralResponse.<List<FineResponseDTO>>builder()
                .message("Your fines retrieved successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fineId}")
    @Operation(summary = "Get fine by ID", description = "Retrieve details of a specific fine by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fine retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Fine not found")
    })
    public ResponseEntity<GeneralResponse<FineResponseDTO>> getFineById(@PathVariable Long fineId) {

        FineResponseDTO data = fineService.getFineById(fineId);

        GeneralResponse<FineResponseDTO> response = GeneralResponse.<FineResponseDTO>builder()
                .message("Fine retrieved successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}
