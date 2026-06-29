package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.PreventiveTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreventiveTaskRepository extends JpaRepository<PreventiveTask, Long> {
    List<PreventiveTask> findByProperty_LandlordIdOrderByScheduledDateAsc(Long landlordId);
    List<PreventiveTask> findByProperty_IdOrderByScheduledDateAsc(Long propertyId);
}
