package com.comedor.modelo.excepciones;

public class SaldoInsuficienteException extends ComedorException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }

    public SaldoInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
