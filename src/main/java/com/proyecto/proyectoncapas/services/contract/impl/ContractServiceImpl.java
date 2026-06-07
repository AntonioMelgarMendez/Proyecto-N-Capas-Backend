package com.proyecto.proyectoncapas.services.contract.impl;

import com.proyecto.proyectoncapas.dto.request.SignContractRequestDTO;
import com.proyecto.proyectoncapas.dto.response.ContractResponseDTO;
import com.proyecto.proyectoncapas.entities.Contract;
import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.exception.ContractAlreadySignedException;
import com.proyecto.proyectoncapas.repository.ContractRepository;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.services.contract.ContractService;
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

        // Check if contract already exists and is signed
        contractRepository.findByReservationId(reservationId).ifPresent(contract -> {
            if ("SIGNED".equals(contract.getStatus())) {
                throw new ContractAlreadySignedException("The contract for this reservation is already signed.");
            }
        });

        Contract contract = new Contract();
        contract.setReservation(reservation);
        contract.setSignedAt(LocalDateTime.now());
        contract.setIpAddress(request.getIpAddress());
        contract.setStatus("SIGNED");

        // Generate Digital Signature (SHA-256 Hash)
        String rawData = reservationId.toString() + contract.getSignedAt().toString() + request.getIpAddress();
        contract.setSignatureHash(generateSHA256Hash(rawData));

        contract = contractRepository.save(contract);
        log.info("Contract digitally signed for Reservation ID: {}", reservationId);

        return mapToDTO(contract);
    }

    @Override
    public ContractResponseDTO getContractByReservationId(Long reservationId) {
        Contract contract = contractRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Contract not found for this reservation"));
        return mapToDTO(contract);
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

    private ContractResponseDTO mapToDTO(Contract contract) {
        return ContractResponseDTO.builder()
                .contractId(contract.getId())
                .reservationId(contract.getReservation().getId())
                .signatureHash(contract.getSignatureHash())
                .signedAt(contract.getSignedAt())
                .status(contract.getStatus())
                .build();
    }
}