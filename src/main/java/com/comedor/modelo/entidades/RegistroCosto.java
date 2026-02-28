package com.comedor.modelo.entidades;

public class RegistroCosto {
    public enum TipoCosto { FIJO, VARIABLE }

    private String periodo;
    private TipoCosto tipo;
    private String descripcion;
    private double monto;

    // Inicializa un registro de costo con periodo, tipo, descripción y monto
    public RegistroCosto(String periodo, TipoCosto tipo, String descripcion, double monto) {
        this.periodo = periodo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.monto = monto;
    }

    // Retorna el periodo del costo (YYYY-MM)
    public String obtPeriodo() { return periodo; }
    
    // Retorna el tipo de costo (FIJO o VARIABLE)
    public TipoCosto obtTipo() { return tipo; }
    
    // Retorna el monto del costo
    public double obtMonto() { return monto; }

    // Retorna una representación en texto del registro de costo
    @Override
    public String toString() {
        return String.format("[%s] %s - %s: $%.2f", periodo, tipo, descripcion, monto);
    }
}