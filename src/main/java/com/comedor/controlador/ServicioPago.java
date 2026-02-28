package com.comedor.controlador;

import com.comedor.modelo.entidades.Monedero;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.persistencia.RepoUsuarios;
import java.util.List;

public class ServicioPago {

    // Verifica el saldo disponible y realiza el descuento si es suficiente, persistiendo el cambio
    public void procesarCobro(Usuario usuario, double monto) throws Exception {
        Monedero monedero = new Monedero(usuario);
        double saldoActual = monedero.obtSaldo();

        if (saldoActual < monto) {
            throw new Exception(String.format("Saldo insuficiente. Costo: $%.2f, Disponible: $%.2f", monto, saldoActual));
        }

        monedero.descontar(monto);

        // Persistir el nuevo saldo (descuento) en la base de datos
        RepoUsuarios repo = new RepoUsuarios();
        List<Usuario> usuarios = repo.listarUsuarios();
        for (Usuario u : usuarios) {
            if (u.obtCedula().equals(usuario.obtCedula())) {
                u.setSaldo(monedero.obtSaldo());
                break;
            }
        }
        repo.guardarTodos(usuarios);
        
        // Actualizar referencia local
        usuario.setSaldo(monedero.obtSaldo());
    }
}