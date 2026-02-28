package com.comedor.modelo.entidades;

public class Costos {
    private String periodo;
    private double costosFijos;
    private double costosVariables;

    // Inicializa un objeto de costos con periodo y montos fijos/variables
    public Costos(String periodo, double costosFijos, double costosVariables) {
        this.periodo = periodo;
        this.costosFijos = costosFijos;
        this.costosVariables = costosVariables;
    }

    // Retorna el periodo asociado a los costos
    public String obtPeriodo() { return periodo; }
    
    // Retorna el monto de los costos fijos
    public double obtCostosFijos() { return costosFijos; }
    
    // Retorna el monto de los costos variables
    public double obtCostosVariables() { return costosVariables; }
    
    // Calcula la suma total de costos fijos y variables
    public double calcularTotal() {
        return costosFijos + costosVariables;
    }
}