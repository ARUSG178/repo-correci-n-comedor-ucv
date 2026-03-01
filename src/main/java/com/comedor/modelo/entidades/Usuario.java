package com.comedor.modelo.entidades;

public abstract class Usuario {
    private String nombre;
    private final String cedula;  
    private String contraseña;
    private boolean estado;
    private int intentosFallidos;
    private double saldo;

    // Inicializa un usuario con valores por defecto
    public Usuario() {
        this.nombre = "";
        this.cedula = "";
        this.contraseña = "";
    }

    // Constructor para registro inicial (Nombre se rellena después desde Secretaría)
    public Usuario(String cedula, String contraseña) {
        this("", cedula, contraseña);
    }
    
    // Inicializa un usuario con credenciales y estado activo por defecto
    public Usuario(String nombre, String cedula, String contraseña) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.contraseña = contraseña;
        this.estado = true;      
        this.intentosFallidos = 0;
        this.saldo = 0.0;
    }

    // Retorna el nombre del usuario
    public String obtNombre() { return nombre; }
    // Retorna el número de cédula del usuario
    public String obtCedula() { return cedula; }
    // Retorna la contraseña del usuario
    public String obtContraseña() { return contraseña; }
    
    // Indica si el usuario está activo o bloqueado
    public boolean obtEstado() { return estado; }
    
    // Retorna el contador de intentos fallidos de inicio de sesión
    public int obtIntentosFallidos() { return intentosFallidos; }
    
    // Retorna el saldo actual disponible en el monedero
    public double obtSaldo() { return saldo; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    // Actualiza la contraseña del usuario
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    
    // Establece el estado de la cuenta (activo/bloqueado)
    public void setEstado(boolean estado) { this.estado = estado; }
    
    // Actualiza el número de intentos fallidos
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }
    
    // Actualiza el saldo del usuario
    public void setSaldo(double saldo) { this.saldo = saldo; }

    // Retorna el tipo de usuario como cadena de texto
    public abstract String obtTipo();

    // Calcula la tarifa final a pagar por el usuario basado en un precio base (CCB o Precio de Venta)
    public abstract double calcularTarifa(double precioBase);
}