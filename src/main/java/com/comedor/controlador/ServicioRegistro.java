package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.persistencia.RepoUsuarios;
import com.comedor.modelo.persistencia.RepoSecretaria;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Profesor;
import com.comedor.modelo.validaciones.VRegistro;
import com.comedor.modelo.persistencia.RepoAdminCdg;
import java.util.List;

public class ServicioRegistro {
    private final RepoUsuarios repositorio;

    // Inicializa el servicio creando una instancia del repositorio de usuarios
    public ServicioRegistro() {
        this.repositorio = new RepoUsuarios();
    }

    // Valida la identidad con Secretaría y guarda la cuenta en la base de datos local
    public void registrarUsuario(Usuario nuevoUsuario) throws InvalidCredentialsException, DuplicateUserException, Exception {
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

    // Método inteligente que detecta el tipo de usuario automáticamente
    public void registrarUsuario(String cedula, String contr, String codigo) throws InvalidCredentialsException, DuplicateUserException, Exception {
        Usuario nuevoUsuario;

        // CASO 1: Registro como Administrador (si se provee código)
        if (codigo != null && !codigo.trim().isEmpty()) {
            nuevoUsuario = new Administrador(cedula, contr, codigo.trim());
        } 
        // CASO 2: Registro Automático (Estudiante, Empleado, Profesor)
        else {
            RepoSecretaria repoSec = new RepoSecretaria();
            Usuario uSecretaria = repoSec.buscarRegistroUCV(cedula);

            if (uSecretaria == null) {
                throw new InvalidCredentialsException("La cédula " + cedula + " no figura en los registros de la UCV.");
            }

            if (uSecretaria instanceof Estudiante) {
                nuevoUsuario = new Estudiante(cedula, contr, "", "");
            } else if (uSecretaria instanceof Profesor) {
                nuevoUsuario = new Profesor(cedula, contr, "", "");
            } else {
                // Aquí entran Empleados
                nuevoUsuario = new Empleado(cedula, contr, "", "", "");
            }
        }

        // Delegamos al método principal que valida y guarda (VRegistro se encargará de llenar los datos faltantes)
        this.registrarUsuario(nuevoUsuario);
    }
}
