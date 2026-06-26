package com.proyecto.proyectoncapas.services.temporalKey;

import com.proyecto.proyectoncapas.dto.response.TemporalKeyResponseDTO;

public interface TemporalKeyService {
    TemporalKeyResponseDTO generateKeyForContract(Long Contract);
}
