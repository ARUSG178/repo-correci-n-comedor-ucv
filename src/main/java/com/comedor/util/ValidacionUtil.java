package com.comedor.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidacionUtil {

    private static final Map<String, List<String>> FACULTADES_CARRERAS = new HashMap<>();

    static {
        FACULTADES_CARRERAS.put("Agronomía", Arrays.asList("Ingeniería Agronómica"));
        FACULTADES_CARRERAS.put("Arquitectura y Urbanismo", Arrays.asList("Arquitectura"));
        FACULTADES_CARRERAS.put("Ciencias", Arrays.asList("Biología", "Computación", "Física", "Geoquímica", "Matemática", "Química"));
        FACULTADES_CARRERAS.put("Ciencias Económicas y Sociales", Arrays.asList("Administración Comercial", "Contaduría Pública", "Antropología", "Economía", "Estadística", "Ciencias Actuariales", "Estudios Internacionales", "Sociología", "Trabajo Social"));
        FACULTADES_CARRERAS.put("Ciencias Jurídicas y Políticas", Arrays.asList("Derecho", "Estudios Políticos"));
        FACULTADES_CARRERAS.put("Ciencias Veterinarias", Arrays.asList("Medicina Veterinaria"));
        FACULTADES_CARRERAS.put("Farmacia", Arrays.asList("Farmacia"));
        FACULTADES_CARRERAS.put("Humanidades y Educación", Arrays.asList("Artes", "Bibliotecología y Archivología", "Comunicación Social", "Educación", "Filosofía", "Geografía", "Historia", "Idiomas Modernos", "Letras", "Psicología"));
        FACULTADES_CARRERAS.put("Ingeniería", Arrays.asList("Ingeniería Civil", "Ingeniería Eléctrica", "Ingeniería Geodésica", "Ingeniería Geofísica", "Ingeniería Geológica", "Ingeniería Hidrometeorológica", "Ingeniería Mecánica", "Ingeniería Metalúrgica", "Ingeniería de Minas", "Ingeniería de Petróleo", "Ingeniería Química"));
        FACULTADES_CARRERAS.put("Medicina", Arrays.asList("Bioanálisis", "Enfermería", "Medicina", "Nutrición y Dietética", "Tecnología Cardiopulmonar", "Fisioterapia", "Terapia Ocupacional", "Radiodiagnóstico"));
        FACULTADES_CARRERAS.put("Odontología", Arrays.asList("Odontología"));
    }

    // Validar formato de cédula
    public static boolean formatoCedula(String cedula) {
        return cedula != null && cedula.matches("\\d{6,10}");
    }

    // Validar formato de contraseña
    public static boolean formatoContraseña(String contraseña) {
        return contraseña != null && contraseña.length() >= 6;
    }

    // Validar facultad y carrera
    public static boolean validarFacultadCarrera(String facultad, String carrera) {
        if (facultad == null || carrera == null) return false;
        
        for (Map.Entry<String, List<String>> entry : FACULTADES_CARRERAS.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(facultad.trim())) {
                for (String c : entry.getValue()) {
                    if (c.equalsIgnoreCase(carrera.trim())) return true;
                }
            }
        }
        return false;
    }
}
