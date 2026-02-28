package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.persistencia.RepoUsuarios;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.validaciones.VRegistro;
import com.comedor.modelo.persistencia.RepoAdminCdg;

import java.io.IOException;
import java.util.List;

public class ServicioRegistro {
    private final RepoUsuarios repositorio;

    // Inicializa el servicio creando una instancia del repositorio de usuarios
    public ServicioRegistro() {
        this.repositorio = new RepoUsuarios();
    }

    // Valida la identidad con Secretaría y guarda la cuenta en la base de datos local
    public void registrarUsuario(Usuario nuevoUsuario) throws InvalidCredentialsException, DuplicateUserException, IOException {
        List<Usuario> usuariosRegistrados = repositorio.listarUsuarios();

        VRegistro validador = new VRegistro(nuevoUsuario, usuariosRegistrados);
        validador.validar();

        repositorio.guardarUsuario(nuevoUsuario);
        if (nuevoUsuario instanceof Administrador) {
            RepoAdminCdg repoCdg = new RepoAdminCdg();
            repoCdg.consumirCodigo(((Administrador) nuevoUsuario).obtCodigoAdministrador());
        }

        System.out.println("Usuario registrado exitosamente en el sistema. Cédula: " + nuevoUsuario.obtCedula());
    }
}
