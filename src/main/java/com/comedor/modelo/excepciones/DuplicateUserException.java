package com.comedor.modelo.excepciones;

public class DuplicateUserException extends Exception{
    public DuplicateUserException(String message) {
        super(message);
    }
}
