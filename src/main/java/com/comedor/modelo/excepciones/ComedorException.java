package com.comedor.modelo.excepciones;

public class ComedorException extends Exception {
    public ComedorException(String message) {
        super(message);
    }

    public ComedorException(String message, Throwable cause) {
        super(message, cause);
    }
}
