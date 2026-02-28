package com.comedor.modelo.entidades;

public class Administrador extends Usuario {
    private String codigoAdministrador; // código de verificación para registro

    // Inicializa un administrador con credenciales y código de verificación
    public Administrador(String cedula, String contraseña, String codigoAdministrador) {
        super(cedula, contraseña);
        this.codigoAdministrador = codigoAdministrador;
    }

    // Inicializa un administrador vacío por defecto
    public Administrador() {
        super(); // Llama al constructor sin parámetros de Usuario
        this.codigoAdministrador = "";
    }

    // Retorna el identificador de tipo para administrador
    @Override
    public String obtTipo() { return "Administrador"; }

    // Retorna el código de verificación del administrador
    public String obtCodigoAdministrador() { return codigoAdministrador; }
    
    // Establece el código de verificación del administrador
    public void setCodigoAdministrador(String codigoAdministrador) { this.codigoAdministrador = codigoAdministrador; }

    // Métodos placeholder para gestión de usuarios
    public void gestionarUsuarios() { }
    
    // Métodos placeholder para gestión de reservas
    public void gestionarReservas() { }

    @Override
    public double calcularTarifa(double precioBase) {
        // Los administradores pagan la tarifa completa (o la lógica que aplique)
        return precioBase;
    }
}