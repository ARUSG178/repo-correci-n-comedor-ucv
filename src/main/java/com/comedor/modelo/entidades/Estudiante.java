package com.comedor.modelo.entidades;

public class Estudiante extends Usuario {
    private String facultad;
    private String carrera;
   

    // Inicializa un estudiante con credenciales y datos académicos
    public Estudiante(String cedula, String contraseña, String carrera, String facultad) {
        super(cedula, contraseña);
        setCarrera(carrera);
        setFacultad(facultad);
    }

    // Retorna el identificador de tipo para estudiante
    @Override
    public String obtTipo() { return "Estudiante"; }

    // Retorna la carrera que cursa el estudiante
    public String obtCarrera() { return carrera; }
    
    // Retorna la facultad a la que pertenece el estudiante
    public String obtFacultad() { return facultad; }
    
    // Establece la carrera del estudiante
    public void setCarrera(String carrera) { this.carrera = carrera; }
    
    // Establece la facultad del estudiante
    public void setFacultad(String facultad) { this.facultad = facultad; }

    @Override
    public double calcularTarifa(double precioBase) {
        // Los estudiantes tienen un subsidio del 80%, pagan solo el 20% del precio base
        return precioBase * 0.20;
    }
}