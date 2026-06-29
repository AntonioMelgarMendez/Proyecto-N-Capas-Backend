package com.proyecto.proyectoncapas.controller.maintenance;

import com.proyecto.proyectoncapas.dto.request.PreventiveTaskRequestDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.PreventiveTaskResponseDTO;
import com.proyecto.proyectoncapas.entities.PreventiveTask;
import com.proyecto.proyectoncapas.entities.Property;
import com.proyecto.proyectoncapas.repository.PreventiveTaskRepository;
import com.proyecto.proyectoncapas.repository.PropertyRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preventive-tasks")
@RequiredArgsConstructor
public class PreventiveTaskController {

    private final PreventiveTaskRepository taskRepository;
    private final PropertyRepository propertyRepository;

    @GetMapping("/landlord/{landlordId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
    public ResponseEntity<GeneralResponse<List<PreventiveTaskResponseDTO>>> getByLandlord(@PathVariable Long landlordId) {
        List<PreventiveTaskResponseDTO> data = taskRepository
                .findByProperty_LandlordIdOrderByScheduledDateAsc(landlordId)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(GeneralResponse.<List<PreventiveTaskResponseDTO>>builder()
                .message("Tasks retrieved")
                .data(data)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
    public ResponseEntity<GeneralResponse<PreventiveTaskResponseDTO>> create(
            @Valid @RequestBody PreventiveTaskRequestDTO req) {

        Property property = propertyRepository.findById(req.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        PreventiveTask task = PreventiveTask.builder()
                .property(property)
                .title(req.getTitle())
                .description(req.getDescription())
                .scheduledDate(req.getScheduledDate())
                .build();

        PreventiveTaskResponseDTO data = toDTO(taskRepository.save(task));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.<PreventiveTaskResponseDTO>builder()
                        .message("Task created")
                        .data(data)
                        .build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
    public ResponseEntity<GeneralResponse<PreventiveTaskResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        PreventiveTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        task.setStatus(status);
        PreventiveTaskResponseDTO data = toDTO(taskRepository.save(task));

        return ResponseEntity.ok(GeneralResponse.<PreventiveTaskResponseDTO>builder()
                .message("Status updated")
                .data(data)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ARRENDADOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private PreventiveTaskResponseDTO toDTO(PreventiveTask t) {
        return PreventiveTaskResponseDTO.builder()
                .id(t.getId())
                .propertyId(t.getProperty().getId())
                .propertyTitle(t.getProperty().getTitle())
                .title(t.getTitle())
                .description(t.getDescription())
                .scheduledDate(t.getScheduledDate())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
