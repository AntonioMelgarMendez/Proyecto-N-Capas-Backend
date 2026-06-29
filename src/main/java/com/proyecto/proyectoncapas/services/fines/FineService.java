package com.proyecto.proyectoncapas.services.fines;

import com.proyecto.proyectoncapas.dto.request.FineRequestDTO;
import com.proyecto.proyectoncapas.dto.request.UpdateFineStatusRequestDTO;
import com.proyecto.proyectoncapas.dto.response.FineResponseDTO;

import java.util.List;

public interface FineService {

    FineResponseDTO createFine(FineRequestDTO requestDTO, Long generatedByUserId);
    FineResponseDTO updateFineStatus(Long fineId, UpdateFineStatusRequestDTO updateFineStatusRequestDTO);
    List<FineResponseDTO> getFineByUser(Long userId);
    List<FineResponseDTO> getAllFines();
    FineResponseDTO getFineById(Long fineId);
}
