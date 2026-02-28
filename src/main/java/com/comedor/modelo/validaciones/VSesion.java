package com.comedor.modelo.validaciones;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;

public class VSesion {
    private final Usuario uIngresado;
    private final Usuario uBaseDatos;

    // Inicializa el validador de sesión con el usuario ingresado y el de base de datos
    public VSesion(Usuario uIngresado, Usuario uBaseDatos) {
        if (uIngresado == null || uBaseDatos == null) {
            throw new IllegalArgumentException("Los objetos de usuario no pueden ser nulos");
        }
        this.uIngresado = uIngresado;
        this.uBaseDatos = uBaseDatos;
    }

    // Verifica si la cédula ingresada coincide con la almacenada
    boolean validarCedula() {
        return uIngresado.obtCedula().trim().equals(uBaseDatos.obtCedula().trim());
    }

    // Verifica si la contraseña ingresada coincide con la almacenada
    boolean validarContraseña() {
        return uIngresado.obtContraseña().equals(uBaseDatos.obtContraseña());
    }

    // Ejecuta la validación de credenciales y gestiona intentos fallidos
    public void validar() throws InvalidCredentialsException {

        if (!(validarCedula() && validarContraseña())) {
            uBaseDatos.setIntentosFallidos(uBaseDatos.obtIntentosFallidos() + 1);

            if (uBaseDatos.obtIntentosFallidos() >= 3) {
                uBaseDatos.setEstado(false);
                throw new InvalidCredentialsException("Cuenta bloqueada por múltiples intentos fallidos");
            }

            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
        }
    }
}