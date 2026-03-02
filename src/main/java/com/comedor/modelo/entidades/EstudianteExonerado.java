package com.comedor.modelo.entidades;

public class EstudianteExonerado extends Estudiante {

    public EstudianteExonerado(String cedula, String contraseña, String carrera, String facultad) {
        super(cedula, contraseña, carrera, facultad);
    }

    @Override
    public String obtTipo() {
        return "EstudianteExonerado";
    }

    @Override
    public double calcularTarifa(double precioBase) {
        return 0.0;
    }
}
