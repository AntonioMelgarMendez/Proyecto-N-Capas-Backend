package com.proyecto.proyectoncapas.utils.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContractSignedEvent extends ApplicationEvent {

    private final Long contractId;

    public ContractSignedEvent(Object source, Long contractId) {
        super(source);
        this.contractId = contractId;
    }
}
