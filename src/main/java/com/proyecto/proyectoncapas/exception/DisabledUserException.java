package com.proyecto.proyectoncapas.exception;

public class DisabledUserException extends RuntimeException {
    public DisabledUserException(String message) {
        super(message);
    }
}
