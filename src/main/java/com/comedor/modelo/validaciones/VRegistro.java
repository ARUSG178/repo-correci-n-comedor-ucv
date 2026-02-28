package com.comedor.modelo.validaciones; 

import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.util.ValidacionUtil;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.persistencia.RepoAdminCdg;
import com.comedor.modelo.persistencia.RepoSecretaria;

import java.io.IOException;
import java.util.List;

public class VRegistro {
    private final Usuario uIngresado;
    private final List<Usuario> usBaseDatos;

    // Inicializa el validador de registro con el usuario a registrar y la lista existente
    public VRegistro(Usuario uIngresado, List<Usuario> usBaseDatos) {
        if (uIngresado == null || usBaseDatos == null) {
            throw new IllegalArgumentException("El usuario y la lista no pueden ser nulos");
        }
        this.uIngresado = uIngresado;
        this.usBaseDatos = usBaseDatos;
    }

    // Verifica que no exista otro usuario con la misma cédula
    boolean validarDuplicado() {
        return usBaseDatos.stream().noneMatch(u -> u.obtCedula().equals(uIngresado.obtCedula()));
    }

    // Valida que los campos específicos de estudiante no estén vacíos
    boolean validarEstudiante() {
        if (uIngresado instanceof Estudiante) {
            Estudiante est = (Estudiante) uIngresado;
            if (est.obtCarrera() == null || est.obtCarrera().isEmpty() || est.obtFacultad() == null || est.obtFacultad().isEmpty()) {
                return false;
            }
            return ValidacionUtil.validarFacultadCarrera(est.obtFacultad(), est.obtCarrera());
        }
        return true;
    }

    // Valida que los campos específicos de empleado no estén vacíos
    boolean validarEmpleado() {
        if (uIngresado instanceof Empleado) {
            Empleado emp = (Empleado) uIngresado;
            return emp.obtCargo() != null && !emp.obtCargo().isEmpty()
                    && emp.obtDepartamento() != null && !emp.obtDepartamento().isEmpty();
        }
        return true;
    }

    // Verifica que la cédula exista en la base de datos de la UCV y coincida el tipo de usuario
    void validarInscripcionUCV() throws InvalidCredentialsException, IOException {
        // Los administradores tienen su propio mecanismo de validación (código)
        if (uIngresado instanceof Administrador) return;

        RepoSecretaria repoSec = new RepoSecretaria();
        Usuario uSecretaria = repoSec.buscarRegistroUCV(uIngresado.obtCedula());

        if (uSecretaria == null) {
            throw new InvalidCredentialsException(
                "La cédula " + uIngresado.obtCedula() + " no figura en los registros activos de la UCV."
            );
        }

        // Validar consistencia de tipo (Ej: No registrarse como Estudiante si en secretaría es Empleado)
        if (!uSecretaria.obtTipo().equalsIgnoreCase(uIngresado.obtTipo())) {
            throw new InvalidCredentialsException(
                "Inconsistencia de datos: La cédula ingresada está registrada en la UCV como " + 
                uSecretaria.obtTipo() + ", pero intenta registrarse como " + uIngresado.obtTipo() + "."
            );
        }

        // Aquí podrías agregar validaciones extra, como que la Carrera coincida con la de secretaría, etc.
    }

    // Ejecuta todas las validaciones necesarias para registrar un usuario
    public void validar() throws InvalidCredentialsException, DuplicateUserException, IOException {
        if (!ValidacionUtil.formatoCedula(uIngresado.obtCedula())) {
            throw new InvalidCredentialsException("Error en el campo Cédula: El formato no es válido. Solo debe contener números.");
        }
        if (!ValidacionUtil.formatoContraseña(uIngresado.obtContraseña())) {
            throw new InvalidCredentialsException("Error en el campo Contraseña: No cumple con los requisitos mínimos (ej. 6 caracteres).");
        }
        if (!validarDuplicado()) {
            throw new DuplicateUserException("El usuario ya existe en la base de datos");
        }
        
        // Validar contra la base de datos simulada de la UCV
        validarInscripcionUCV();

        if (uIngresado instanceof Administrador) {
            String codigo = ((Administrador) uIngresado).obtCodigoAdministrador();
            if (codigo == null || !codigo.trim().matches("[A-Za-z0-9]{8}")) {
                throw new InvalidCredentialsException("Error en el campo Código Admin: El código debe ser alfanumérico de 8 caracteres.");
            }
            RepoAdminCdg repoCdg = new RepoAdminCdg();
            if (!repoCdg.existeCodigo(codigo.trim())) {
                throw new InvalidCredentialsException("Error en el campo Código Admin: El código de administrador no fue encontrado o no es válido.");
            }
        }
        if (!validarEstudiante()) {
            throw new InvalidCredentialsException("Error en los datos de Estudiante: La Facultad y/o Carrera son inválidas o no se corresponden.");
        }
        if (!validarEmpleado()) {
            throw new InvalidCredentialsException("Error en los datos de Empleado: El Cargo y/o Departamento no pueden estar vacíos.");
        }
    }


}