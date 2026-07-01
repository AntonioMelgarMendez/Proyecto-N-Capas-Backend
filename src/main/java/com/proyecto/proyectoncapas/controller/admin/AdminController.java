package com.proyecto.proyectoncapas.controller.admin;

import com.proyecto.proyectoncapas.dto.response.AdminStatsDTO;
import com.proyecto.proyectoncapas.dto.response.GeneralResponse;
import com.proyecto.proyectoncapas.dto.response.PropertyResponseDTO;
import com.proyecto.proyectoncapas.dto.response.UserResponseDTO;
import com.proyecto.proyectoncapas.entities.Property;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.repository.PropertyRepository;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.utils.mappers.PropertyMapper;
import com.proyecto.proyectoncapas.utils.mappers.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin / Management", description = "Administration operations to manage users, properties, and system statistics")
public class AdminController {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ReservationRepository reservationRepository;

    @GetMapping("/stats")
    @Operation(summary = "Get platform stats", description = "Retrieve general metrics of total users, active users, total properties, available properties, and total reservations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics successfully retrieved")
    })
    public ResponseEntity<GeneralResponse<AdminStatsDTO>> getStats() {
        AdminStatsDTO stats = AdminStatsDTO.builder()
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countByIsActiveTrue())
                .totalProperties(propertyRepository.count())
                .availableProperties(propertyRepository.countByIsAvailableTrue())
                .totalReservations(reservationRepository.count())
                .build();

        return ResponseEntity.ok(GeneralResponse.<AdminStatsDTO>builder()
                .message("Stats retrieved")
                .data(stats)
                .build());
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users in the platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users successfully retrieved")
    })
    public ResponseEntity<GeneralResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userRepository.findAll()
                .stream()
                .map(UserMapper::userResponseDTO)
                .toList();

        return ResponseEntity.ok(GeneralResponse.<List<UserResponseDTO>>builder()
                .message("Users retrieved")
                .data(users)
                .build());
    }

    @PatchMapping("/users/{id}/toggle-status")
    @Operation(summary = "Toggle user status", description = "Enable or disable a user account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status toggled successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<GeneralResponse<UserResponseDTO>> toggleUserStatus(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setIsActive(!user.getIsActive());
        User saved = userRepository.save(user);

        return ResponseEntity.ok(GeneralResponse.<UserResponseDTO>builder()
                .message("Estado actualizado")
                .data(UserMapper.userResponseDTO(saved))
                .build());
    }

    @GetMapping("/properties")
    @Operation(summary = "Get all properties", description = "Retrieve a list of all properties registered in the platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Properties successfully retrieved")
    })
    public ResponseEntity<GeneralResponse<List<PropertyResponseDTO>>> getAllProperties() {
        List<PropertyResponseDTO> properties = propertyRepository.findAll()
                .stream()
                .map(PropertyMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(GeneralResponse.<List<PropertyResponseDTO>>builder()
                .message("Properties retrieved")
                .data(properties)
                .build());
    }

    @PatchMapping("/properties/{id}/toggle-availability")
    @Operation(summary = "Toggle property availability", description = "Enable or disable property availability for booking by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property availability toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<GeneralResponse<PropertyResponseDTO>> togglePropertyAvailability(@PathVariable Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        property.setIsAvailable(!property.getIsAvailable());
        Property saved = propertyRepository.save(property);

        return ResponseEntity.ok(GeneralResponse.<PropertyResponseDTO>builder()
                .message("Disponibilidad actualizada")
                .data(PropertyMapper.toResponseDTO(saved))
                .build());
    }
}
