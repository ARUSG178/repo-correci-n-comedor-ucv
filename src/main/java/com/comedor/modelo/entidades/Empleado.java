package com.comedor.modelo.entidades;

public class Empleado extends Usuario {
    private String cargo;         
    private String departamento;  
    private String codigoEmpleado; 

    // Inicializa un empleado con credenciales y datos laborales
    public Empleado(String cedula, String contraseña, String cargo, String departamento, String codigoEmpleado) {
        super(cedula, contraseña);
        setCargo(cargo);
        setDepartamento(departamento);
        setCodigoEmpleado(codigoEmpleado);
    }

    // Retorna el identificador de tipo para empleado
    @Override
    public String obtTipo() { return "Empleado"; }

    // Retorna el cargo que ocupa el empleado
    public String obtCargo() { return cargo; }
    
    // Retorna el departamento al que pertenece el empleado
    public String obtDepartamento() { return departamento; }
    
    // Retorna el código único de empleado
    public String obtCodigoEmpleado() { return codigoEmpleado; }

    // Establece el cargo del empleado
    public void setCargo(String cargo) { this.cargo = cargo; }
    
    // Establece el departamento del empleado
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    
    // Establece el código de empleado
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }

    @Override
    public double calcularTarifa(double precioBase) {
        // Los empleados tienen un subsidio del 50%, pagan la mitad del precio base
        return precioBase * 0.50;
    }
}