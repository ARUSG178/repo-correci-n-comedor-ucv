package com.comedor.modelo.persistencia;

import java.io.IOException;
import java.util.List;

import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.EstudianteBecario;
import com.comedor.modelo.entidades.EstudianteExonerado;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Profesor;
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
                    usuario.setNombre(nombre); // Guardamos el nombre leído
                    return usuario;
                }
            }
        }
        return null; // No encontrado en la UCV
    }
}