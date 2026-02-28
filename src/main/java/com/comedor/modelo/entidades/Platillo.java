package com.comedor.modelo.entidades;

public class Platillo {
    private String nombre;
    private String descripcion;
    private double precio;
    private boolean disponible;

    // Inicializa un platillo vacío por defecto
    public Platillo() {
        this.nombre = "";
        this.precio = 0.0;
    }
    
    // Inicializa un platillo con nombre, descripción y precio
    public Platillo(String nombre, String descripcion, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = true;
    }

    // Retorna el nombre del platillo
    public String obtNombre() { return nombre; }
    
    // Retorna la descripción del platillo
    public String obtDescripcion() { return descripcion; }
    
    // Retorna el precio del platillo
    public double obtPrecio() { return precio; }
    
    // Indica si el platillo está disponible
    public boolean obtDisponible() { return disponible; }
    
    // Establece el nombre del platillo
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    // Establece la disponibilidad del platillo
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    // Establece el precio del platillo
    public void setPrecio(double precio) { this.precio = precio; }

    @Override
    public String toString() {
        return nombre + " ($" + precio + ")";
    }
}
