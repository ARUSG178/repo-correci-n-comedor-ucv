package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.validaciones.VSesion;
import com.comedor.modelo.persistencia.RepoUsuarios;

import java.io.IOException;
import java.util.List;

public class ServicioIS {

    // Autentica al usuario verificando credenciales y tipo de cuenta en la base de datos
    public Usuario IniciarSesion(Usuario uIngresado) throws InvalidCredentialsException, IOException {
        RepoUsuarios repo = new RepoUsuarios();
        List<Usuario> usuarios = repo.listarUsuarios();

        Usuario uBD = usuarioPorCedula(uIngresado.obtCedula(), usuarios);

        if (uBD == null) {
            throw new InvalidCredentialsException("Usuario no encontrado");
        }

        VSesion validador = new VSesion(uIngresado, uBD);
        try {
            validador.validar();
            pLoginExitoso(uBD, usuarios, repo);
            return uBD;
        } catch (InvalidCredentialsException ex) {
            pLoginFallido(usuarios, repo);
            throw ex;
        }
    }

    // Busca un usuario en la lista por su cédula
    private Usuario usuarioPorCedula(String cedula, List<Usuario> usuarios) {
        return usuarios.stream()
                .filter(u -> u != null && u.obtCedula() != null && 
                             u.obtCedula().trim().equalsIgnoreCase(cedula.trim()))
                .findFirst()
                .orElse(null);
    }

    // Procesa un inicio de sesión exitoso, reseteando intentos y guardando
    private void pLoginExitoso(Usuario usuario, List<Usuario> todosLosUsuarios, RepoUsuarios repo) throws IOException {
        System.out.println("LOG: Inicio de sesión exitoso para cédula: " + usuario.obtCedula());
        usuario.setIntentosFallidos(0);
        repo.guardarTodos(todosLosUsuarios);
    }

    // Procesa un inicio de sesión fallido, guardando el estado actualizado del usuario
    private void pLoginFallido(List<Usuario> todosLosUsuarios, RepoUsuarios repo) throws IOException {
        // VSesion ya modificó el objeto usuario en la lista, solo persistimos.
        repo.guardarTodos(todosLosUsuarios);
    }
}