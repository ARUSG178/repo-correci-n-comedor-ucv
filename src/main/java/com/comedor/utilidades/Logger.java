package com.comedor.utilidades;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad centralizada para logging en el sistema SAGC UCV
 * Reemplaza todos los System.out.println y maneja errores de forma consistente
 */
public class Logger {
    private static final String LOG_FILE = "sagc_log.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public enum Level {
        INFO, WARNING, ERROR, DEBUG
    }
    
    /**
     * Registra un mensaje con nivel INFO
     */
    public static void info(String mensaje) {
        log(Level.INFO, mensaje, null);
    }
    
    /**
     * Registra un mensaje con nivel WARNING
     */
    public static void warning(String mensaje) {
        log(Level.WARNING, mensaje, null);
    }
    
    /**
     * Registra un mensaje con nivel ERROR
     */
    public static void error(String mensaje) {
        log(Level.ERROR, mensaje, null);
    }
    
    /**
     * Registra un mensaje con nivel ERROR incluyendo la excepción
     */
    public static void error(String mensaje, Exception excepcion) {
        log(Level.ERROR, mensaje, excepcion);
    }
    
    /**
     * Registra un mensaje con nivel DEBUG
     */
    public static void debug(String mensaje) {
        log(Level.DEBUG, mensaje, null);
    }
    
    /**
     * Método central de logging
     */
    private static void log(Level nivel, String mensaje, Exception excepcion) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String logEntry = String.format("[%s] %s: %s", timestamp, nivel, mensaje);
        
        // Escribir a consola para desarrollo
        System.out.println(logEntry);
        
        // Escribir a archivo para producción
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(logEntry);
            
            if (excepcion != null) {
                excepcion.printStackTrace(writer);
            }
            
            writer.flush();
        } catch (IOException e) {
            // Si falla el logging, al menos mostrarlo en consola
            System.err.println("ERROR: No se pudo escribir en el archivo de log: " + e.getMessage());
        }
    }
    
    /**
     * Limpia el archivo de log (útil para pruebas o reinicio del sistema)
     */
    public static void limpiarLog() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, false))) {
            writer.print("");
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo limpiar el archivo de log: " + e.getMessage());
        }
    }
}
