package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.PropertyRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRuleRepository extends JpaRepository<PropertyRule, Long> {

    List<PropertyRule> findByPropertyId(Long propertyId);

    void deleteByPropertyId(Long propertyId);
}
