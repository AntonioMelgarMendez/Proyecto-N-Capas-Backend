package com.proyecto.proyectoncapas.utils.mappers;
import com.proyecto.proyectoncapas.dto.response.ContractResponseDTO;
import com.proyecto.proyectoncapas.entities.Contract;

public class ContractMapper {

    public static ContractResponseDTO toDTO(Contract contract){
        return ContractResponseDTO.builder()
                .contractId(contract.getId())
                .reservationId(contract.getReservation() != null ? contract.getReservation().getId() : null)
                .content(contract.getContent())
                .signatureHash(contract.getSignatureHash())
                .tenantSignatureDate(contract.getTenantSignatureDate())
                .landlordSignatureDate(contract.getLandlordSignatureDate())
                .status(contract.getStatus())
                .build();
    }
}