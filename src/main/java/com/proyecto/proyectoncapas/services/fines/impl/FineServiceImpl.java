package com.proyecto.proyectoncapas.services.fines.impl;

import com.proyecto.proyectoncapas.dto.request.FineRequestDTO;
import com.proyecto.proyectoncapas.dto.request.UpdateFineStatusRequestDTO;
import com.proyecto.proyectoncapas.dto.response.FineResponseDTO;
import com.proyecto.proyectoncapas.entities.Fine;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.FineRepository;
import com.proyecto.proyectoncapas.repository.UserRepository;
import com.proyecto.proyectoncapas.services.fines.FineService;
import com.proyecto.proyectoncapas.utils.mappers.FineMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FineResponseDTO createFine(FineRequestDTO fineRequestDTO, Long generatedByUserId) {

        User infractor = userRepository.findById(fineRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario infractor no encontrado con id: " + fineRequestDTO.getUserId()));

        User generatedBy = userRepository.findById(generatedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario generador de infracción no encontrado con id: " + generatedByUserId));

        Fine fine = Fine.builder()
                .user(infractor)
                .generatedBy(generatedBy)
                .infractionType(fineRequestDTO.getInfractionType())
                .amount(fineRequestDTO.getAmount())
                .description(fineRequestDTO.getDescription())
                .infractionDate(fineRequestDTO.getInfractionDate() != null
                        ? fineRequestDTO.getInfractionDate() : LocalDateTime.now())
                .build();

        Fine savedFine = fineRepository.save(fine);
        return FineMapper.toResponseDTO(savedFine);
    }


    @Override
    @Transactional
    public FineResponseDTO updateFineStatus(Long fineId, UpdateFineStatusRequestDTO updateFineStatusRequestDTO) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("Multa no encontrada con id: " + fineId));

        fine.setStatus(updateFineStatusRequestDTO.getFineStatus());
        Fine updatedFine = fineRepository.save(fine);
        return FineMapper.toResponseDTO(updatedFine);
    }

    @Override
    public List<FineResponseDTO> getFineByUser(Long userId) {
        return fineRepository.findByUserId(userId)
                .stream()
                .map(FineMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<FineResponseDTO> getAllFines() {
        return fineRepository.findAll()
                .stream()
                .map(FineMapper::toResponseDTO)
                .toList();
    }

    @Override
    public FineResponseDTO getFineById(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("Multa no encontrada con id: " + fineId));
        return FineMapper.toResponseDTO(fine);
    }
}
