package com.comedor.modelo.excepciones;

public class BiometriaFallidaException extends ComedorException {
    public BiometriaFallidaException(String message) {
        super(message);
    }

    public BiometriaFallidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
