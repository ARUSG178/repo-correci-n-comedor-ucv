package com.comedor.modelo.entidades;

public class Profesor extends Usuario {
    private String departamento;
    private String codigo;

    public Profesor(String cedula, String contraseña, String departamento, String codigo) {
        super(cedula, contraseña);
        this.departamento = departamento;
        this.codigo = codigo;
    }

    @Override
    public String obtTipo() {
        return "Profesor";
    }

    public String obtDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String obtCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public double calcularTarifa(double precioBase) {
        return precioBase; // Profesores pagan tarifa completa (100%)
    }
}