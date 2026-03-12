package com.comedor.modelo.persistencia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.EstudianteBecario;
import com.comedor.modelo.entidades.EstudianteExonerado;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Profesor;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.util.ServicioUtil;

public class RepoSecretaria implements IRepositorioSecretaria {
    private static final String RUTA_ARCHIVO = "src/main/java/com/comedor/data/secretaria_ucv.txt";

    // Busca una cédula en la base de datos simulada de la UCV y retorna el usuario con sus datos oficiales
    public Usuario buscarRegistroUCV(String cedulaBuscada) throws IOException {
        List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);

        for (String linea : lineas) {
            String[] datos = linea.split(";");
            if (datos.length < 2) continue;

            String tipo = datos[0];
            String cedula = datos[1];
            String nombre = (datos.length > 2) ? datos[2].trim() : "";

            if (cedula.trim().equals(cedulaBuscada.trim())) {
                // Encontramos la cédula, reconstruimos el objeto para validaros
                Usuario usuario = null;

                if (tipo.equalsIgnoreCase("Estudiante") && datos.length >= 5) {
                    String carrera = datos[3].trim();
                    String facultad = datos[4].trim();
                    usuario = new Estudiante(cedula, nombre, cedula, carrera, facultad);
                } 
                else if (tipo.equalsIgnoreCase("Empleado") && datos.length >= 6) {
                    String cargo = datos[3].trim();
                    String depto = datos[4].trim();
                    String codigo = datos[5].trim();
                    
                    if (cargo.equalsIgnoreCase("Profesor")) {
                        usuario = new Profesor(cedula, cedula, depto, codigo);
                        usuario.setNombre(nombre);
                    } else {
                        usuario = new Empleado(cedula, cedula, cargo, depto, codigo);
                        usuario.setNombre(nombre);
                    }
                }
                else if (tipo.equalsIgnoreCase("Profesor") && datos.length >= 5) {
                    String depto = datos[3].trim();
                    String codigo = datos[4].trim();
                    usuario = new Profesor(cedula, cedula, depto, codigo);
                    usuario.setNombre(nombre);
                }
                else if (tipo.equalsIgnoreCase("EstudianteBecario") && datos.length >= 6) {
                    String carrera = datos[3].trim();
                    String facultad = datos[4].trim();
                    double descuento = 95.0;
                    try {
                        descuento = Double.parseDouble(datos[5].trim());
                    } catch (NumberFormatException e) {
                        descuento = 95.0;
                    }
                    usuario = new EstudianteBecario(cedula, cedula, carrera, facultad, descuento);
                    usuario.setNombre(nombre);
                }
                else if (tipo.equalsIgnoreCase("EstudianteExonerado") && datos.length >= 5) {
                    String carrera = datos[3].trim();
                    String facultad = datos[4].trim();
                    usuario = new EstudianteExonerado(cedula, cedula, carrera, facultad);
                    usuario.setNombre(nombre);
                }
                else if (tipo.equalsIgnoreCase("Administrador") && datos.length >= 4) {
                    String codigoAdmin = datos[3].trim();
                    usuario = new Administrador(cedula, cedula, codigoAdmin);
                    usuario.setNombre(nombre);
                }

                if (usuario != null) {
                    return usuario;
                }
            }
        }
        return null; // No encontrado en la UCV
    }

    // Listar todos los registros de la UCV
    public List<Usuario> listarTodos() throws IOException {
        List<Usuario> usuarios = new ArrayList<>();
        List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);

        for (String linea : lineas) {
            String[] datos = linea.split(";");
            if (datos.length < 2) continue;

            String tipo = datos[0];
            String cedula = datos[1];
            String nombre = (datos.length > 2) ? datos[2].trim() : "";

            Usuario usuario = crearUsuarioDesdeDatos(tipo, cedula, nombre, datos);
            if (usuario != null) {
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    // Guardar un nuevo usuario en el archivo
    public void guardar(Usuario usuario, String contraseña) throws IOException {
        String linea = serializarUsuario(usuario, contraseña);
        ServicioUtil.escribirLinea(RUTA_ARCHIVO, linea, true);
    }

    // Actualizar un usuario existente
    public void actualizar(Usuario usuario, String contraseña) throws IOException {
        List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);
        List<String> nuevasLineas = new ArrayList<>();
        boolean encontrado = false;

        for (String linea : lineas) {
            String[] datos = linea.split(";");
            if (datos.length >= 2 && datos[1].trim().equals(usuario.obtCedula())) {
                // Reemplazar esta línea
                nuevasLineas.add(serializarUsuario(usuario, contraseña));
                encontrado = true;
            } else {
                nuevasLineas.add(linea);
            }
        }

        if (!encontrado) {
            throw new IOException("Usuario no encontrado: " + usuario.obtCedula());
        }

        // Reescribir todo el archivo
        escribirTodasLasLineas(RUTA_ARCHIVO, nuevasLineas);
    }

    // Eliminar un usuario por cédula
    public void eliminar(String cedula) throws IOException {
        List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);
        List<String> nuevasLineas = new ArrayList<>();
        boolean encontrado = false;

        for (String linea : lineas) {
            String[] datos = linea.split(";");
            if (datos.length >= 2 && datos[1].trim().equals(cedula)) {
                encontrado = true;
                // No agregar esta línea (eliminar)
            } else {
                nuevasLineas.add(linea);
            }
        }

        if (!encontrado) {
            throw new IOException("Usuario no encontrado: " + cedula);
        }

        // Reescribir todo el archivo
        escribirTodasLasLineas(RUTA_ARCHIVO, nuevasLineas);
    }

    // Verificar si existe una cédula
    public boolean existeCedula(String cedula) throws IOException {
        List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);
        for (String linea : lineas) {
            String[] datos = linea.split(";");
            if (datos.length >= 2 && datos[1].trim().equals(cedula)) {
                return true;
            }
        }
        return false;
    }

    // Métodos auxiliares
    private Usuario crearUsuarioDesdeDatos(String tipo, String cedula, String nombre, String[] datos) {
        Usuario usuario = null;

        if (tipo.equalsIgnoreCase("Estudiante") && datos.length >= 5) {
            String carrera = datos[3].trim();
            String facultad = datos[4].trim();
            usuario = new Estudiante(cedula, "", carrera, facultad);
        } 
        else if (tipo.equalsIgnoreCase("Empleado") && datos.length >= 6) {
            String cargo = datos[3].trim();
            String depto = datos[4].trim();
            String codigo = datos[5].trim();
            
            if (cargo.equalsIgnoreCase("Profesor")) {
                usuario = new Profesor(cedula, "", depto, codigo);
            } else {
                usuario = new Empleado(cedula, "", cargo, depto, codigo);
            }
        }
        else if (tipo.equalsIgnoreCase("Profesor") && datos.length >= 5) {
            String depto = datos[3].trim();
            String codigo = datos[4].trim();
            usuario = new Profesor(cedula, "", depto, codigo);
        }
        else if (tipo.equalsIgnoreCase("EstudianteBecario") && datos.length >= 5) {
            String carrera = datos[3].trim();
            String facultad = datos[4].trim();
            usuario = new EstudianteBecario(cedula, "", carrera, facultad, 95.0);
        }
        else if (tipo.equalsIgnoreCase("EstudianteExonerado") && datos.length >= 5) {
            String carrera = datos[3].trim();
            String facultad = datos[4].trim();
            usuario = new EstudianteExonerado(cedula, "", carrera, facultad);
        }

        if (usuario != null) {
            usuario.setNombre(nombre);
        }
        return usuario;
    }

    private String serializarUsuario(Usuario usuario, String contraseña) {
        StringBuilder sb = new StringBuilder();
        
        if (usuario instanceof EstudianteBecario) {
            EstudianteBecario eb = (EstudianteBecario) usuario;
            sb.append("EstudianteBecario;");
            sb.append(usuario.obtCedula()).append(";");
            sb.append(usuario.obtNombre()).append(";");
            sb.append(((Estudiante)usuario).obtCarrera()).append(";");
            sb.append(((Estudiante)usuario).obtFacultad()).append(";");
            sb.append(eb.obtPorcentajeDescuento());
        }
        else if (usuario instanceof EstudianteExonerado) {
            sb.append("EstudianteExonerado;");
            sb.append(usuario.obtCedula()).append(";");
            sb.append(usuario.obtNombre()).append(";");
            sb.append(((Estudiante)usuario).obtCarrera()).append(";");
            sb.append(((Estudiante)usuario).obtFacultad());
        }
        else if (usuario instanceof Estudiante) {
            sb.append("Estudiante;");
            sb.append(usuario.obtCedula()).append(";");
            sb.append(usuario.obtNombre()).append(";");
            sb.append(((Estudiante)usuario).obtCarrera()).append(";");
            sb.append(((Estudiante)usuario).obtFacultad());
        }
        else if (usuario instanceof Profesor) {
            Profesor p = (Profesor) usuario;
            sb.append("Profesor;");
            sb.append(usuario.obtCedula()).append(";");
            sb.append(usuario.obtNombre()).append(";");
            sb.append(p.obtDepartamento()).append(";");
            sb.append(p.obtCodigo());  // Ahora contiene la materia
        }
        else if (usuario instanceof Empleado) {
            Empleado e = (Empleado) usuario;
            sb.append("Empleado;");
            sb.append(usuario.obtCedula()).append(";");
            sb.append(usuario.obtNombre()).append(";");
            sb.append(e.obtCargo()).append(";");
            sb.append(e.obtDepartamento()).append(";");
            sb.append(e.obtCodigoEmpleado());
        }
        else if (usuario instanceof Administrador) {
            Administrador a = (Administrador) usuario;
            sb.append("Administrador;");
            sb.append(usuario.obtCedula()).append(";");
            sb.append(usuario.obtNombre()).append(";");
            sb.append(a.obtCodigoAdministrador());
        }
        
        String resultado = sb.toString();
        return resultado;
    }

    // Método auxiliar para escribir todas las líneas (reemplaza el archivo)
    private void escribirTodasLasLineas(String ruta, List<String> lineas) throws IOException {
        ServicioUtil.garantizarArchivo(ruta);
        java.nio.file.Path path = java.nio.file.Paths.get(ruta);
        try (java.io.BufferedWriter escritor = java.nio.file.Files.newBufferedWriter(path, 
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE, 
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String linea : lineas) {
                escritor.write(linea);
                escritor.newLine();
            }
        }
    }
}