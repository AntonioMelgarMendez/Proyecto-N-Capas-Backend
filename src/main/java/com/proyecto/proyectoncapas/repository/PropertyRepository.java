package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByLandlordId(Long landlordId);

    List<Property> findByIsAvailableTrue();

    List<Property> findByCityIgnoreCase(String city);
}
