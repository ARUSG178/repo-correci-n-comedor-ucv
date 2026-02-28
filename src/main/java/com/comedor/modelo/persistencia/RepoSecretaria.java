package com.comedor.modelo.persistencia;

import java.io.IOException;
import java.util.List;

import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.util.ServicioUtil;

public class RepoSecretaria {
    private static final String RUTA_ARCHIVO = "src/main/java/com/comedor/data/secretaria_ucv.txt";

    // Busca una cédula en la base de datos simulada de la UCV y retorna el usuario con sus datos oficiales
    public Usuario buscarRegistroUCV(String cedulaBuscada) throws IOException {
        List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);

        for (String linea : lineas) {
            String[] datos = linea.split(";");
            if (datos.length < 2) continue;

            String tipo = datos[0];
            String cedula = datos[1];

            if (cedula.trim().equals(cedulaBuscada.trim())) {
                // Encontramos la cédula, reconstruimos el objeto para validaros

                if (tipo.equalsIgnoreCase("Estudiante") && datos.length >= 5) {
                    String carrera = datos[3];
                    String facultad = datos[4];
                    // Retornamos un objeto Estudiante con los datos de secretaría
                    return new Estudiante(cedula, "", carrera, facultad);
                } 
                else if (tipo.equalsIgnoreCase("Empleado") && datos.length >= 6) {
                    String cargo = datos[3];
                    String depto = datos[4];
                    String codigo = datos[5];
                    return new Empleado(cedula, "", cargo, depto, codigo);
                }
            }
        }
        return null; // No encontrado en la UCV
    }
}