package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.IdentityDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument, Long> {
    Optional<IdentityDocument> findByUserId(Long userId);
}