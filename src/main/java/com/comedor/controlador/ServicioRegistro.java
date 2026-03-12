package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.persistencia.IRepositorioUsuarios;
import com.comedor.modelo.persistencia.IRepositorioSecretaria;
import com.comedor.modelo.persistencia.IRepositorioAdminCdg;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Profesor;
import com.comedor.modelo.validaciones.VRegistro;
import java.util.List;

/**
 * Servicio de registro que aplica el Principio de Inversión de Dependencias (DIP).
 * Recibe las dependencias por constructor en lugar de instanciarlas directamente.
 */
public class ServicioRegistro {
    private final IRepositorioUsuarios repositorio;
    private final IRepositorioSecretaria repoSecretaria;
    private final IRepositorioAdminCdg repoAdminCdg;

    /**
     * Constructor que permite inyección de dependencias.
     * @param repositorio El repositorio de usuarios a usar
     * @param repoSecretaria El repositorio de secretaria a usar
     * @param repoAdminCdg El repositorio de códigos admin a usar
     */
    public ServicioRegistro(IRepositorioUsuarios repositorio, 
                            IRepositorioSecretaria repoSecretaria,
                            IRepositorioAdminCdg repoAdminCdg) {
        this.repositorio = repositorio;
        this.repoSecretaria = repoSecretaria;
        this.repoAdminCdg = repoAdminCdg;
    }

    // Valida la identidad con Secretaría y guarda la cuenta en la base de datos local
    public void registrarUsuario(Usuario nuevoUsuario) throws InvalidCredentialsException, DuplicateUserException, Exception {
        List<Usuario> usuariosRegistrados = repositorio.listarUsuarios();

        VRegistro validador = new VRegistro(nuevoUsuario, usuariosRegistrados, repoSecretaria, repoAdminCdg);
        validador.validar();

        repositorio.guardarUsuario(nuevoUsuario);
        if (nuevoUsuario instanceof Administrador) {
            repoAdminCdg.consumirCodigo(((Administrador) nuevoUsuario).obtCodigoAdministrador());
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
            Usuario uSecretaria = repoSecretaria.buscarRegistroUCV(cedula);

            if (uSecretaria == null) {
                throw new InvalidCredentialsException("La cédula " + cedula + " no figura en los registros de la UCV.");
            }

            // Obtener datos desde secretaría
            String nombre = uSecretaria.obtNombre();
            
            if (uSecretaria instanceof Estudiante) {
                String carrera = ((Estudiante) uSecretaria).obtCarrera();
                String facultad = ((Estudiante) uSecretaria).obtFacultad();
                nuevoUsuario = new Estudiante(cedula, contr, carrera, facultad);
                nuevoUsuario.setNombre(nombre);
            } else if (uSecretaria instanceof Profesor) {
                String depto = ((Profesor) uSecretaria).obtDepartamento();
                String materia = ((Profesor) uSecretaria).obtCodigo();
                nuevoUsuario = new Profesor(cedula, contr, depto, materia);
                nuevoUsuario.setNombre(nombre);
            } else if (uSecretaria instanceof Empleado) {
                String cargo = ((Empleado) uSecretaria).obtCargo();
                String depto = ((Empleado) uSecretaria).obtDepartamento();
                String codEmp = ((Empleado) uSecretaria).obtCodigoEmpleado();
                nuevoUsuario = new Empleado(cedula, contr, cargo, depto, codEmp);
                nuevoUsuario.setNombre(nombre);
            } else {
                // Fallback: crear como Estudiante genérico
                nuevoUsuario = new Estudiante(cedula, contr, "", "");
                nuevoUsuario.setNombre(nombre);
            }
        }

        // Delegamos al método principal que valida y guarda
        this.registrarUsuario(nuevoUsuario);
    }
}
