package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.validaciones.VSesion;
import com.comedor.modelo.persistencia.IRepositorioUsuarios;
import com.comedor.utilidades.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Servicio de inicio de sesión que aplica el Principio de Inversión de Dependencias (DIP).
 * Recibe el repositorio por constructor en lugar de instanciarlo directamente.
 */
public class ServicioIS {
    private final IRepositorioUsuarios repositorio;
    
    /**
     * Constructor que permite inyección de dependencias.
     * @param repositorio El repositorio de usuarios a usar
     */
    public ServicioIS(IRepositorioUsuarios repositorio) {
        this.repositorio = repositorio;
    }

    // Autentica al usuario verificando credenciales y tipo de cuenta en la base de datos
    public Usuario IniciarSesion(Usuario uIngresado) throws InvalidCredentialsException, IOException {
        List<Usuario> usuarios = repositorio.listarUsuarios();

        Usuario uBD = repositorio.buscarPorCedula(uIngresado.obtCedula());

        if (uBD == null) {
            throw new InvalidCredentialsException("Usuario no encontrado");
        }

        VSesion validador = new VSesion(uIngresado, uBD);
        try {
            validador.validar();
            pLoginExitoso(uBD, usuarios);
            return uBD;
        } catch (InvalidCredentialsException ex) {
            pLoginFallido(usuarios);
            throw ex;
        }
    }

    // Procesa un inicio de sesión exitoso, reseteando intentos y guardando
    private void pLoginExitoso(Usuario usuario, List<Usuario> todosLosUsuarios) throws IOException {
        Logger.info("Inicio de sesión exitoso para cédula: " + usuario.obtCedula());
        usuario.setIntentosFallidos(0);
        repositorio.guardarTodos(todosLosUsuarios);
    }

    // Procesa un inicio de sesión fallido, guardando el estado actualizado del usuario
    private void pLoginFallido(List<Usuario> todosLosUsuarios) throws IOException {
        // VSesion ya modificó el objeto usuario en la lista, solo persistimos.
        repositorio.guardarTodos(todosLosUsuarios);
    }
}