package com.comedor.modelo.entidades;

public class EstudianteBecario extends Estudiante {

    public EstudianteBecario(String cedula, String contraseña, String carrera, String facultad) {
        super(cedula, contraseña, carrera, facultad);
    }

    @Override
    public String obtTipo() {
        return "EstudianteBecario";
    }

    @Override
    public double calcularTarifa(double precioBase) {
        return precioBase * 0.05;
    }
}
