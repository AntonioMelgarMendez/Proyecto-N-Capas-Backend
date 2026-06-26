package com.proyecto.proyectoncapas.utils.events;

import com.proyecto.proyectoncapas.services.temporalKey.TemporalKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemporalKeyServiceListener {

    private final TemporalKeyService temporalKeyService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onContractSigned(ContractSignedEvent event) {
        try {
            temporalKeyService.generateKeyForContract(event.getContractId());
            log.info("Llave temporal generada automaticamente para contrato {}", event.getContractId());
        }
        catch (Exception e) {
            log.error("No se pudo generar la llave temporal para el contrato {}: {}",
                    event.getContractId(), e.getMessage());
        }
    }
}
