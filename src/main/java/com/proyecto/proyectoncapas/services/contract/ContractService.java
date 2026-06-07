package com.proyecto.proyectoncapas.services.contract;

import com.proyecto.proyectoncapas.dto.request.SignContractRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ContractResponseDTO;

public interface ContractService {
    ContractResponseDTO signContract(Long reservationId, SignContractRequestDTO request);
    ContractResponseDTO getContractByReservationId(Long reservationId);
}