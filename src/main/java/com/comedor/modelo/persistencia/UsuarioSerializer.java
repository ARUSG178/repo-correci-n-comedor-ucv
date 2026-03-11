package com.comedor.modelo.persistencia;

import com.comedor.modelo.entidades.*;

/**
 * Clase responsable de serializar y deserializar usuarios a/desde formato CSV.
 */
public class UsuarioSerializer {
    
    /**
     * Convierte un objeto Usuario a su representación en formato CSV.
     * @param usuario El usuario a serializar
     * @return La representación CSV del usuario
     */
    public String serializar(Usuario usuario) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(usuario.obtTipo()).append(";");
        buffer.append(usuario.obtCedula()).append(";");
        buffer.append(usuario.obtNombre()).append(";");
        buffer.append(usuario.obtContraseña()).append(";");
        buffer.append(usuario.obtEstado()).append(";");
        buffer.append(usuario.obtIntentosFallidos()).append(";");
        buffer.append(usuario.obtSaldo()).append(";");
        
        // Serializar campos específicos según el tipo
        if (usuario instanceof EstudianteBecario) {
            EstudianteBecario eb = (EstudianteBecario) usuario;
            buffer.append(eb.obtCarrera()).append(";");
            buffer.append(eb.obtFacultad()).append(";");
            buffer.append(eb.obtPorcentajeDescuento()); // Guardar % descuento
        } else if (usuario instanceof EstudianteExonerado) {
            EstudianteExonerado ee = (EstudianteExonerado) usuario;
            buffer.append(ee.obtCarrera()).append(";");
            buffer.append(ee.obtFacultad());
        } else if (usuario instanceof Estudiante) {
            Estudiante estudiante = (Estudiante) usuario;
            buffer.append(estudiante.obtCarrera()).append(";");
            buffer.append(estudiante.obtFacultad());
        } else if (usuario instanceof Empleado) {
            Empleado emp = (Empleado) usuario;
            buffer.append(emp.obtCargo()).append(";");
            buffer.append(emp.obtDepartamento()).append(";");
            buffer.append(emp.obtCodigoEmpleado());
        } else if (usuario instanceof Profesor) {
            Profesor prof = (Profesor) usuario;
            buffer.append(prof.obtDepartamento()).append(";");
            buffer.append(prof.obtCodigo());
        } else if (usuario instanceof Administrador) {
            Administrador adm = (Administrador) usuario;
            buffer.append(adm.obtCodigoAdministrador());
        }
        
        return buffer.toString();
    }
    
    /**
     * Convierte una línea de texto CSV a un objeto Usuario.
     * @param linea La línea CSV a deserializar
     * @return El usuario deserializado o null si la línea es inválida
     */
    public Usuario deserializar(String linea) {
        String[] datos = linea.split(";");
        if (datos.length < 7) return null;
        
        String tipo = datos[0].trim();
        String cedula = datos[1].trim();
        String nombre = datos[2].trim();
        String contraseña = datos[3].trim();
        boolean estado = Boolean.parseBoolean(datos[4].trim());
        int intentosFallidos = Integer.parseInt(datos[5].trim());
        double saldo = Double.parseDouble(datos[6].trim());
        
        Usuario usuario = null;
        
        switch (tipo) {
            case "Estudiante":
                if (datos.length >= 9) {
                    String carrera = datos[7].trim();
                    String facultad = datos[8].trim();
                    usuario = new Estudiante(cedula, contraseña, carrera, facultad);
                }
                break;
            case "EstudianteBecario":
                if (datos.length >= 9) {
                    String carrera = datos[7].trim();
                    String facultad = datos[8].trim();
                    double porcentajeDescuento = 95.0; // Valor por defecto
                    if (datos.length >= 10) {
                        try {
                            porcentajeDescuento = Double.parseDouble(datos[9].trim());
                        } catch (NumberFormatException e) {
                            porcentajeDescuento = 95.0;
                        }
                    }
                    usuario = new EstudianteBecario(cedula, contraseña, carrera, facultad, porcentajeDescuento);
                }
                break;
            case "EstudianteExonerado":
                if (datos.length >= 9) {
                    String carrera = datos[7].trim();
                    String facultad = datos[8].trim();
                    usuario = new EstudianteExonerado(cedula, contraseña, carrera, facultad);
                }
                break;
            case "Empleado":
                if (datos.length >= 10) {
                    String cargo = datos[7].trim();
                    String departamento = datos[8].trim();
                    String codigo = datos[9].trim();
                    usuario = new Empleado(cedula, contraseña, cargo, departamento, codigo);
                }
                break;
            case "Profesor":
                if (datos.length >= 9) {
                    String departamento = datos[7].trim();
                    String codigo = datos[8].trim();
                    usuario = new Profesor(cedula, contraseña, departamento, codigo);
                }
                break;
            case "Administrador":
                if (datos.length >= 8) {
                    String codigoAdmin = datos[7].trim();
                    usuario = new Administrador(cedula, contraseña, codigoAdmin);
                }
                break;
        }
        
        if (usuario != null) {
            usuario.setNombre(nombre);
            usuario.setEstado(estado);
            usuario.setIntentosFallidos(intentosFallidos);
            usuario.setSaldo(saldo);
        }
        
        return usuario;
    }
}
