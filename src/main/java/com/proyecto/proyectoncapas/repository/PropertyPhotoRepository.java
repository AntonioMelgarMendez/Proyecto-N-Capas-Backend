package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.PropertyPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyPhotoRepository extends JpaRepository<PropertyPhoto, Long> {

    List<PropertyPhoto> findByPropertyId(Long propertyId);

    Optional<PropertyPhoto> findByPropertyIdAndIsPrimaryTrue(Long propertyId);

    void deleteByPropertyId(Long propertyId);
}
