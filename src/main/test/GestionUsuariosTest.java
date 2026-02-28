package com.comedor.test.gestionusuarios;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.persistencia.RepoUsuarios;

/**
 * Pruebas de caja negra para la gestión de Usuarios
 * Técnicas aplicadas:
 * - Partición de equivalencia
 * - Análisis de valores límite
 */

public class GestionUsuariosTest {

    private RepoUsuarios repo;
    private Estudiante estudianteValido;
    private Empleado empleadoValido;
    private Administrador adminValido;
    
    @TempDir
    Path tempDir;
    
    private String rutaArchivoOriginal;
    private File archivoTemp;

    @BeforeEach
    void setUp() throws Exception {
        repo = new RepoUsuarios();
        
        // Crear usuarios válidos para pruebas
        estudianteValido = new Estudiante(
            "V-12345678", 
            "password123", 
            "Ingeniería Informática", 
            "Facultad de Ingeniería"
        );
        estudianteValido.setEstado(true);
        estudianteValido.setIntentosFallidos(0);
        estudianteValido.setSaldo(100.0);
        
        empleadoValido = new Empleado(
            "V-87654321", 
            "password456", 
            "Profesor", 
            "Departamento de Computación",
            "EMP-001"
        );
        empleadoValido.setEstado(true);
        empleadoValido.setIntentosFallidos(0);
        empleadoValido.setSaldo(200.0);
        
        adminValido = new Administrador(
            "V-11223344", 
            "admin123", 
            "ADMIN-001"
        );
        adminValido.setEstado(true);
        adminValido.setIntentosFallidos(0);
        adminValido.setSaldo(0.0);
        
        // Redirigir archivo temporal para pruebas
        archivoTemp = new File(tempDir.toFile(), "usuarios_test.txt");
        java.lang.reflect.Field field = RepoUsuarios.class.getDeclaredField("RUTA_ARCHIVO");
        field.setAccessible(true);
        rutaArchivoOriginal = (String) field.get(null);
        field.set(null, archivoTemp.getAbsolutePath());
    }

    @AfterEach
    void tearDown() throws Exception {
        // Restaurar ruta original
        java.lang.reflect.Field field = RepoUsuarios.class.getDeclaredField("RUTA_ARCHIVO");
        field.setAccessible(true);
        field.set(null, rutaArchivoOriginal);
        
        if (archivoTemp.exists()) {
            archivoTemp.delete();
        }
    }

    // ============================================
    // PRUEBA 1: guardarUsuario() - Partición de equivalencia
    // ============================================
    
    @Test
    @DisplayName("G1: guardarUsuario - Partición de equivalencia")
    void testGuardarUsuario() throws IOException {
        // CASO 1: Guardar usuario válido (partición válida)
        repo.guardarUsuario(estudianteValido);
        
        List<Usuario> usuarios = repo.listarUsuarios();
        assertEquals(1, usuarios.size(), "Debería guardar 1 usuario válido");
        
        Usuario recuperado = usuarios.get(0);
        assertEquals("Estudiante", recuperado.obtTipo());
        assertEquals("V-12345678", recuperado.obtCedula());
        assertEquals(100.0, recuperado.obtSaldo());
        
        // CASO 2: Guardar múltiples usuarios
        repo.guardarUsuario(empleadoValido);
        repo.guardarUsuario(adminValido);
        
        usuarios = repo.listarUsuarios();
        assertEquals(3, usuarios.size(), "Debería guardar múltiples usuarios");
        
        // Verificar que se guardaron en orden
        assertTrue(usuarios.get(0) instanceof Estudiante);
        assertTrue(usuarios.get(1) instanceof Empleado);
        assertTrue(usuarios.get(2) instanceof Administrador);
    }
    
    @Test
    @DisplayName("G1: guardarUsuario - Valores límite")
    void testGuardarUsuarioValoresLimite() throws IOException {
        // CASO: Usuario con valores en los límites
        
        // Estudiante con cédula mínima
        Estudiante estudianteMin = new Estudiante("V-1", "pass", "Carrera", "Facultad");
        estudianteMin.setSaldo(0.0); // Saldo mínimo
        estudianteMin.setIntentosFallidos(0); // Intentos mínimos
        
        // Estudiante con cédula máxima (larga)
        Estudiante estudianteMax = new Estudiante(
            "V-9999999999", 
            "pass", 
            "Carrera muy larga con muchos caracteres que podría superar el límite esperado", 
            "Facultad igualmente larga para probar los límites del sistema"
        );
        estudianteMax.setSaldo(999999.99); // Saldo máximo
        estudianteMax.setIntentosFallidos(5); // Intentos máximos (límite típico)
        
        repo.guardarUsuario(estudianteMin);
        repo.guardarUsuario(estudianteMax);
        
        List<Usuario> usuarios = repo.listarUsuarios();
        assertEquals(2, usuarios.size());
        
        Usuario recuperadoMin = usuarios.get(0);
        assertEquals("V-1", recuperadoMin.obtCedula());
        assertEquals(0.0, recuperadoMin.obtSaldo());
        assertEquals(0, recuperadoMin.obtIntentosFallidos());
        
        Usuario recuperadoMax = usuarios.get(1);
        assertEquals("V-9999999999", recuperadoMax.obtCedula());
        assertEquals(999999.99, recuperadoMax.obtSaldo());
        assertEquals(5, recuperadoMax.obtIntentosFallidos());
    }

    // ============================================
    // PRUEBA 2: listarUsuarios() - Partición de equivalencia
    // ============================================
    
    @Test
    @DisplayName("G3: listarUsuarios - Partición de equivalencia")
    void testListarUsuarios() throws IOException {
        // CASO 1: Archivo vacío
        List<Usuario> vacio = repo.listarUsuarios();
        assertTrue(vacio.isEmpty());
        
        // CASO 2: Archivo con usuarios válidos
        repo.guardarUsuario(estudianteValido);
        repo.guardarUsuario(empleadoValido);
        
        List<Usuario> usuarios = repo.listarUsuarios();
        assertEquals(2, usuarios.size());
        
        // CASO 3: Archivo con líneas corruptas (simulado)
        try (FileWriter fw = new FileWriter(archivoTemp, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("línea corrupta sin punto y coma");
            bw.newLine();
        }
        
        // Debería ignorar líneas corruptas y seguir funcionando
        usuarios = repo.listarUsuarios();
        assertEquals(2, usuarios.size(), "Las líneas corruptas deben ignorarse");
    }
    
    @Test
    @DisplayName("G3: listarUsuarios - Recuperación correcta de todos los tipos")
    void testListarUsuariosRecuperaTipos() throws IOException {
        repo.guardarUsuario(estudianteValido);
        repo.guardarUsuario(empleadoValido);
        repo.guardarUsuario(adminValido);
        
        List<Usuario> usuarios = repo.listarUsuarios();
        
        // Verificar que cada tipo mantiene sus datos específicos
        Estudiante est = (Estudiante) usuarios.stream()
            .filter(u -> u instanceof Estudiante)
            .findFirst().orElse(null);
        assertNotNull(est);
        assertEquals("Ingeniería Informática", est.obtCarrera());
        assertEquals("Facultad de Ingeniería", est.obtFacultad());
        
        Empleado emp = (Empleado) usuarios.stream()
            .filter(u -> u instanceof Empleado)
            .findFirst().orElse(null);
        assertNotNull(emp);
        assertEquals("Profesor", emp.obtCargo());
        assertEquals("Departamento de Computación", emp.obtDepartamento());
        assertEquals("EMP-001", emp.obtCodigoEmpleado());
        
        Administrador adm = (Administrador) usuarios.stream()
            .filter(u -> u instanceof Administrador)
            .findFirst().orElse(null);
        assertNotNull(adm);
        assertEquals("ADMIN-001", adm.obtCodigoAdministrador());
    }
}