package com.comedor.modelo.entidades;

public class Monedero {
    private Usuario propietario;
    public static final double LIMITE_SALDO = 9999.0;

    // Inicializa el monedero asociándolo a un usuario propietario
    public Monedero(Usuario propietario) {
        this.propietario = propietario;
    }

    // Retorna el usuario propietario del monedero
    public Usuario obtPropietario() { return propietario; }
    
    // Obtiene el saldo actual directamente del usuario propietario
    public double obtSaldo() { return propietario.obtSaldo(); }

    // Aumenta el saldo del propietario si el monto es positivo
    public void recargar(double monto) {
        if (monto > 0 && (propietario.obtSaldo() + monto) <= LIMITE_SALDO) {
            propietario.setSaldo(propietario.obtSaldo() + monto);
        }
    }
    
    // Descuenta el monto del saldo si hay fondos suficientes
    public boolean descontar(double monto) {
        if (monto > 0 && obtSaldo() >= monto) {
            propietario.setSaldo(obtSaldo() - monto);
            return true;
        }
        return false;
    }

    // Retorna una representación en texto del estado del monedero
    @Override
    public String toString() {
        return "Monedero de " + propietario.obtCedula() + " [Saldo: $" + obtSaldo() + "]";
    }
}
