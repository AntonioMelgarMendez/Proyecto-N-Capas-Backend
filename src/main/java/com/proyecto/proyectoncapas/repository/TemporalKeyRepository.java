package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.TemporalKey;
import com.proyecto.proyectoncapas.utils.enums.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TemporalKeyRepository extends JpaRepository<TemporalKey, Long> {
    List<TemporalKey> findByUserId(Long userId);
    Optional<TemporalKey> findByTempKeyIdAndUserId(Long tempKeyId, Long userId);
    List<TemporalKey> findByUserIdAndRevokedFalseAndUsedFalse(Long userId);
    // Usado por el job @Scheduled para revocar llaves vencidas
    List<TemporalKey> findByValidUntilBeforeAndRevokedFalse(LocalDateTime now);
}
