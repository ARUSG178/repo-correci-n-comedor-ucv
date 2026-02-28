package com.comedor.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioUtil {

    //Asegura que el archivo y sus directorios padres existan.
    public static void garantizarArchivo(String ruta) throws IOException {
        Path path = Paths.get(ruta);
        if (path.getParent() != null && !Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    //Escribe una línea al final del archivo.
     
    public static void escribirLinea(String ruta, String linea, boolean append) throws IOException {
        garantizarArchivo(ruta);
        Path path = Paths.get(ruta);
        StandardOpenOption opcion = append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING;
        
        try (BufferedWriter escritor = Files.newBufferedWriter(path, StandardCharsets.UTF_8, 
                StandardOpenOption.CREATE, opcion)) {
            escritor.write(linea);
            escritor.newLine();
        }
    }

    // Lee todas las líneas de un archivo.
     
    public static List<String> leerLineas(String ruta) throws IOException {
        Path path = Paths.get(ruta);
        List<String> lineas = new ArrayList<>();
        if (!Files.exists(path)) return lineas;

        try (BufferedReader lector = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }
        }
        return lineas;
    }

    // --- SECCIÓN DE FECHAS ---

    //Retorna el periodo actual en formato "YYYY-MM".
     
    public static String obtenerPeriodoActual() {
        LocalDate hoy = LocalDate.now();
        return hoy.getYear() + "-" + String.format("%02d", hoy.getMonthValue());
    }

    // --- SECCIÓN DE MONEDA ---

    //Formatea un double a String de moneda (Ej: "$ 1,250.50").
     
    public static String formatearMoneda(double monto) {
        return String.format("$ %,.2f", monto);
    }

    //Parsea un String de moneda a double, manejando "$" y comas.
     
    public static double parsearMoneda(String texto) throws NumberFormatException {
        String limpio = texto.replace("$", "").replace("USD", "").replace(",", "").trim();
        return Double.parseDouble(limpio);
    }
}