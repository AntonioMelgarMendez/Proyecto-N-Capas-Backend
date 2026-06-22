package com.proyecto.proyectoncapas.controller.fine;

import com.proyecto.proyectoncapas.dto.request.FineRequestDTO;
import com.proyecto.proyectoncapas.dto.request.UpdateFineStatusRequestDTO;
import com.proyecto.proyectoncapas.dto.response.FineResponseDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.services.fines.FineService;
import com.proyecto.proyectoncapas.utils.security.AuthenticatedUserProvider;
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
public class FineController {

    private final FineService fineService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
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
    public ResponseEntity<GeneralResponse<List<FineResponseDTO>>> getAllFines() {

        List<FineResponseDTO> data = fineService.getAllFines();

        GeneralResponse<List<FineResponseDTO>> response = GeneralResponse.<List<FineResponseDTO>>builder()
                .message("Fines retrieved successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
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
    public ResponseEntity<GeneralResponse<FineResponseDTO>> getFineById(@PathVariable Long fineId) {

        FineResponseDTO data = fineService.getFineById(fineId);

        GeneralResponse<FineResponseDTO> response = GeneralResponse.<FineResponseDTO>builder()
                .message("Fine retrieved successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}
