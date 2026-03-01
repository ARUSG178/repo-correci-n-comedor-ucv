package com.comedor.modelo.entidades;

public class Platillo {
    private String nombre;
    private String descripcion;
    private double precio;
    private boolean disponible;
    private String imagen;
    private String infoNutricional;

    // Inicializa un platillo vacío por defecto
    public Platillo() {
        this.nombre = "";
        this.precio = 0.0;
        this.disponible = true;
    }

    // Constructor simplificado (para pruebas o carga rápida)
    public Platillo(String nombre, double precio) {
        this(nombre, "", precio, "", "");
    }
    
    // Inicializa un platillo con nombre, descripción y precio
    public Platillo(String nombre, String descripcion, double precio) {
        this(nombre, descripcion, precio, "", "");
    }

    // Constructor completo con imagen e información nutricional
    public Platillo(String nombre, String descripcion, double precio, String imagen, String infoNutricional) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.infoNutricional = infoNutricional;
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
    
    // Retorna la ruta de la imagen
    public String obtImagen() { return imagen; }

    // Retorna la información nutricional
    public String obtInfoNutricional() { return infoNutricional; }

    // Establece el nombre del platillo
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    // Establece la disponibilidad del platillo
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    // Establece el precio del platillo
    public void setPrecio(double precio) { this.precio = precio; }
    
    // Establece la descripción
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // Establece la ruta de la imagen
    public void setImagen(String imagen) { this.imagen = imagen; }

    // Establece la información nutricional
    public void setInfoNutricional(String infoNutricional) { this.infoNutricional = infoNutricional; }

    @Override
    public String toString() {
        return nombre + " ($" + precio + ")";
    }
}
