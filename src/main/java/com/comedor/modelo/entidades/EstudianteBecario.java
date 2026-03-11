package com.comedor.modelo.entidades;

public class EstudianteBecario extends Estudiante {
    
    private double porcentajeDescuento; // Porcentaje de descuento (ej: 95 = paga 5%)
    
    public EstudianteBecario(String cedula, String contraseña, String carrera, String facultad, double porcentajeDescuento) {
        super(cedula, contraseña, carrera, facultad);
        this.porcentajeDescuento = porcentajeDescuento;
    }
    
    // Constructor por defecto con 95% descuento (paga 5%)
    public EstudianteBecario(String cedula, String contraseña, String carrera, String facultad) {
        this(cedula, contraseña, carrera, facultad, 95.0);
    }

    @Override
    public String obtTipo() {
        return "EstudianteBecario";
    }
    
    public double obtPorcentajeDescuento() {
        return porcentajeDescuento;
    }
    
    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    @Override
    public double calcularTarifa(double precioBase) {
        // Paga el (100 - porcentajeDescuento)% del precio base
        return precioBase * ((100 - porcentajeDescuento) / 100.0);
    }
}
