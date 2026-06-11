package com.proyecto.proyectoncapas.services.reservation.impl;

import com.proyecto.proyectoncapas.repository.PropertyAvailabilityRepository;
import com.proyecto.proyectoncapas.services.reservation.AvailabilityService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {
    private final PropertyAvailabilityRepository availabilityRepository;

    @Transactional
    public List<LocalDate> getOccupiedCalendar(Long propertyId, LocalDate start, LocalDate end) {
        // Retorna directamente los días bloqueados en la BD de manera ultra veloz (O(N) indexado)
        return availabilityRepository.findOccupiedDates(propertyId, start, end);
    }
}