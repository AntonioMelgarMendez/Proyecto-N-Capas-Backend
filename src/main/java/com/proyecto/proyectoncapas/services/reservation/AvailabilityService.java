package com.proyecto.proyectoncapas.services.reservation;


import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {
    List<LocalDate> getOccupiedCalendar(Long propertyId, LocalDate start, LocalDate end);
}