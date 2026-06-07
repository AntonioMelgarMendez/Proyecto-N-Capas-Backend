package com.proyecto.proyectoncapas.services.contract.impl;

import com.proyecto.proyectoncapas.dto.request.SignContractRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ContractResponseDTO;
import com.proyecto.proyectoncapas.entities.Contract;
import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.exception.ContractAlreadySignedException;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.ContractRepository;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.services.contract.ContractService;
import com.proyecto.proyectoncapas.utils.mappers.ContractMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public ContractResponseDTO signContract(Long reservationId, SignContractRequestDTO request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        contractRepository.findByReservationId(reservationId).ifPresent(contract -> {
            if ("SIGNED".equals(contract.getStatus()) || "PENDING_SIGNATURE".equals(contract.getStatus())) {
                throw new ContractAlreadySignedException("The contract is already signed or pending landlord signature.");
            }
        });

        Contract contract = new Contract();
        contract.setReservation(reservation);
        contract.setContent("Legal Rental Agreement for Property...");
        contract.setTenantSignatureDate(LocalDateTime.now());
        contract.setIpAddress(request.getIpAddress());
        contract.setStatus("PENDING_SIGNATURE");
        String rawData = reservationId.toString() + contract.getTenantSignatureDate().toString() + request.getIpAddress();
        contract.setSignatureHash(generateSHA256Hash(rawData));

        contract = contractRepository.save(contract);
        log.info("Contract digitally signed by tenant for Reservation ID: {}", reservationId);

        return ContractMapper.toDTO(contract);
    }

    @Override
    public ContractResponseDTO getContractByReservationId(Long reservationId) {
        Contract contract = contractRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found for Reservation ID: " + reservationId));

        return ContractMapper.toDTO(contract);
    }

    private String generateSHA256Hash(String originalString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Hashing algorithm not found", e);
            throw new RuntimeException("Error generating digital signature");
        }
    }


}