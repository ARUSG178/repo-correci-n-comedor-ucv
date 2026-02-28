package com.comedor.modelo.persistencia;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.util.ServicioUtil;

public class RepoUsuarios {
    private static final String RUTA_ARCHIVO = "src/main/java/com/comedor/data/usuarios.txt";

    // Guarda un usuario al final del archivo de persistencia
    public void guardarUsuario(Usuario usuario) throws IOException {
        ServicioUtil.escribirLinea(RUTA_ARCHIVO, serializar(usuario), true);
    }

    // Sobrescribe el archivo con la lista completa de usuarios actualizada
    public void guardarTodos(List<Usuario> usuarios) throws IOException {
        boolean primeraLinea = true;
        for (Usuario usuario : usuarios) {
            ServicioUtil.escribirLinea(RUTA_ARCHIVO, serializar(usuario), !primeraLinea);
            primeraLinea = false;
        }
    }

    // Lee y retorna todos los usuarios registrados en el archivo
    public List<Usuario> listarUsuarios() throws IOException {
        List<Usuario> listaUsuarios = new ArrayList<>();
        List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);
        
        for (String linea : lineas) {
            Usuario u = deserializar(linea);
            if (u != null) listaUsuarios.add(u);
        }
        return listaUsuarios;
    }

    // Convierte un objeto Usuario a su representación en formato CSV
    private String serializar(Usuario usuario) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(usuario.obtTipo()).append(";");
        buffer.append(usuario.obtCedula()).append(";");
        buffer.append(usuario.obtContraseña()).append(";");
        buffer.append(usuario.obtEstado()).append(";");
        buffer.append(usuario.obtIntentosFallidos()).append(";");
        buffer.append(usuario.obtSaldo()).append(";");

        if (usuario instanceof Estudiante) {
            Estudiante estudiante = (Estudiante) usuario;
            buffer.append(estudiante.obtCarrera()).append(";");
            buffer.append(estudiante.obtFacultad());
        } else if (usuario instanceof Empleado) {
            Empleado emp = (Empleado) usuario;
            buffer.append(emp.obtCargo()).append(";");
            buffer.append(emp.obtDepartamento()).append(";");
            buffer.append(emp.obtCodigoEmpleado());
        } else if (usuario instanceof Administrador) {
            Administrador adm = (Administrador) usuario;
            buffer.append(adm.obtCodigoAdministrador());
        }
        return buffer.toString();
    }

    // Convierte una línea de texto CSV a un objeto Usuario
    private Usuario deserializar(String linea) {
        String[] datos = linea.split(";");
        if (datos.length < 6) return null;

        try {
            String tipo = datos[0];
            String cedula = datos[1];
            String contraseña = datos[2];
            boolean estado = Boolean.parseBoolean(datos[3]);
            int intentos = Integer.parseInt(datos[4]);
            double saldo = Double.parseDouble(datos[5]);

            Usuario usuario = null;

            if (tipo.equals("Estudiante") && datos.length >= 8) {
                usuario = new Estudiante(cedula, contraseña, datos[6], datos[7]);
            } else if (tipo.equals("Empleado") && datos.length >= 9) {
                usuario = new Empleado(cedula, contraseña, datos[6], datos[7], datos[8]);
            } else if (tipo.equals("Administrador") && datos.length >= 7) {
                usuario = new Administrador(cedula, contraseña, datos[6]);
            }

            if (usuario != null) {
                usuario.setEstado(estado);
                usuario.setIntentosFallidos(intentos);
                usuario.setSaldo(saldo);
            }
            return usuario;
        } catch (RuntimeException ex) {
            return null;
        }
    }
}