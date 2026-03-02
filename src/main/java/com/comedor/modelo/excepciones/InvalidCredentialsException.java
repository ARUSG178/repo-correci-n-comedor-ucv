package com.comedor.modelo.excepciones;

public class InvalidCredentialsException extends ComedorException {
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
