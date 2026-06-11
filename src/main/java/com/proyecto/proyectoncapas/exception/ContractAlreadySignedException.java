package com.proyecto.proyectoncapas.exception;

public class ContractAlreadySignedException extends RuntimeException {
    public ContractAlreadySignedException(String message) {
        super(message);
    }
}