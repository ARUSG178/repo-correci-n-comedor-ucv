package com.comedor.modelo.excepciones;

public class DuplicateUserException extends ComedorException {
    public DuplicateUserException(String message) {
        super(message);
    }

    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
