package com.comedor.modelo.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepoAdminCdg {
    private static final String RUTA_ARCHIVO = "src/main/java/com/comedor/data/codigos_admin.txt";

    // Verifica si un código de administrador existe en el archivo
    public boolean existeCodigo(String codigo) {
        try {
            Path path = Paths.get(RUTA_ARCHIVO);
            if (!Files.exists(path)) return false;
            String contenido = Files.readString(path, StandardCharsets.UTF_8);
            return Arrays.stream(contenido.split(";"))
                    .anyMatch(c -> c.trim().equals(codigo));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Elimina un código de administrador del archivo tras su uso
    public void consumirCodigo(String codigo) {
        try {
            Path path = Paths.get(RUTA_ARCHIVO);
            if (!Files.exists(path)) return;
            String contenido = Files.readString(path, StandardCharsets.UTF_8);
            List<String> lista = new ArrayList<>(Arrays.asList(contenido.split(";")));
            lista.removeIf(c -> c.trim().equals(codigo));
            String nuevoContenido = String.join(";", lista);
            Files.writeString(path, nuevoContenido, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}