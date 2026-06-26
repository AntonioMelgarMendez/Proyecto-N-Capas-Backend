package com.proyecto.proyectoncapas.services.temporalKey.impl;

import com.proyecto.proyectoncapas.dto.response.TemporalKeyResponseDTO;
import com.proyecto.proyectoncapas.entities.Contract;
import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.entities.User;
import com.proyecto.proyectoncapas.exception.ContractNotEligibleException;
import com.proyecto.proyectoncapas.exception.ResourceNotFoundException;
import com.proyecto.proyectoncapas.repository.ContractRepository;
import com.proyecto.proyectoncapas.repository.ReservationRepository;
import com.proyecto.proyectoncapas.services.temporalKey.TemporalKeyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class TemporalKeyServiceImpl implements TemporalKeyService {

    private final ContractRepository contractRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public TemporalKeyResponseDTO generateKeyForContract(Long contractId){
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con id: " + contractId));

        if (!"SIGNED".equals(contract.getStatus())) {
            throw new ContractNotEligibleException("El contrato debe estar firmado para generar un PIN de acceso");
        }

        Reservation reservation = contract.getReservation();
        if (reservation == null || reservation.getTenant() == null ) {
            throw new ContractNotEligibleException("El contrato no tiene una reserva o inquilino asociado válido");
        }

        User tenant  = reservation.getTenant();
        String rawPin = generateSixDigitPin();

        reservation.setPinHash(passwordEncoder.encode(rawPin));
        Reservation savedReservation = reservationRepository.save(reservation);


        return TemporalKeyResponseDTO.builder()
                .reservationId(savedReservation.getId())
                .pin(rawPin)
                .validFrom(savedReservation.getCheckInDate())
                .validUntil(savedReservation.getCheckOutDate())
                .build();
    }

    private String generateSixDigitPin() {
        int pin = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(pin);
    }

}
