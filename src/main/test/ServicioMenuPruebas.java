package com.comedor.pruebas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.comedor.controlador.ServicioMenu;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Menu;
import com.comedor.modelo.entidades.Platillo;
import com.comedor.modelo.entidades.Usuario;

/**
 *  * Pruebas de caja negra para el Menu
 * Técnicas aplicadas:
 * - Partición de equivalencia (tipos de usuario)
 * - Valores límite (listas de platillos)
 */
public class ServicioMenuPruebas {

    private ServicioMenu servicio;
    private Usuario administrador;
    private Usuario estudiante;
    private Usuario empleado;
    
    @BeforeEach
    void setUp() {
        // Inicializar servicio antes de cada prueba
        servicio = new ServicioMenu();
        
        // Crear usuarios de prueba
        administrador = new Administrador("1001", "Admin Prueba");
        estudiante = new Estudiante("2001", "Estudiante Prueba");
        empleado = new Empleado("3001", "Empleado Prueba");
    }
    
    /**
     * =========================================================================
     * PRUEBAS DE PARTICIÓN DE EQUIVALENCIA - TIPOS DE USUARIO
     * =========================================================================
     */
    
    @Test
    @DisplayName("CP-01: Administrador configura menú de Desayuno exitosamente")
    void testConfigurarMenu_Admin_Desayuno() {
        // Arrange - Preparar datos de prueba
        Menu nuevoMenu = new Menu("Desayuno");
        nuevoMenu.setNombre("Desayuno Nutritivo");
        nuevoMenu.setMenuID(101);
        nuevoMenu.setFechaInicio(LocalDate.now());
        nuevoMenu.setFechaFin(LocalDate.now().plusDays(7));
        nuevoMenu.setEstado("ACTIVO");
        
        List<Platillo> platillos = new ArrayList<>();
        platillos.add(new Platillo("Huevos revueltos", 2500.0));
        platillos.add(new Platillo("Jugo de naranja", 1500.0));
        platillos.add(new Platillo("Arepa con queso", 1800.0));
        nuevoMenu.agregarPlatillos(platillos);
        
        // Act - Ejecutar el método a probar
        servicio.configurarMenu(administrador, nuevoMenu);
        Menu menuActualizado = servicio.obtenerMenu("Desayuno");
        
        // Assert - Verificar resultados
        assertAll("Verificar configuración completa del menú de desayuno",
            () -> assertEquals("Desayuno Nutritivo", menuActualizado.obtNombre(), 
                "El nombre del menú no se actualizó correctamente"),
            () -> assertEquals(101, menuActualizado.obtMenuID(), 
                "El ID del menú no se actualizó correctamente"),
            () -> assertEquals(3, menuActualizado.obtPlatillos().size(), 
                "La cantidad de platillos no coincide"),
            () -> assertEquals("ACTIVO", menuActualizado.obtEstado(), 
                "El estado del menú no se actualizó correctamente"),
            () -> assertNotNull(menuActualizado.obtFechaInicio(), 
                "La fecha de inicio no debería ser null"),
            () -> assertNotNull(menuActualizado.obtFechaFin(), 
                "La fecha de fin no debería ser null")
        );
    }
    
    @Test
    @DisplayName("CP-02: Administrador configura menú de Almuerzo exitosamente")
    void testConfigurarMenu_Admin_Almuerzo() {
        // Arrange
        Menu nuevoMenu = new Menu("Almuerzo");
        nuevoMenu.setNombre("Almuerzo Ejecutivo");
        nuevoMenu.setMenuID(201);
        nuevoMenu.setFechaInicio(LocalDate.now());
        nuevoMenu.setFechaFin(LocalDate.now().plusDays(7));
        nuevoMenu.setEstado("ACTIVO");
        
        List<Platillo> platillos = new ArrayList<>();
        platillos.add(new Platillo("Pollo a la plancha", 4500.0));
        platillos.add(new Platillo("Ensalada mixta", 1200.0));
        platillos.add(new Platillo("Arroz con coco", 800.0));
        platillos.add(new Platillo("Jugo natural", 1500.0));
        nuevoMenu.agregarPlatillos(platillos);
        
        // Act
        servicio.configurarMenu(administrador, nuevoMenu);
        Menu menuActualizado = servicio.obtenerMenu("Almuerzo");
        
        // Assert
        assertAll("Verificar configuración completa del menú de almuerzo",
            () -> assertEquals("Almuerzo Ejecutivo", menuActualizado.obtNombre()),
            () -> assertEquals(201, menuActualizado.obtMenuID()),
            () -> assertEquals(4, menuActualizado.obtPlatillos().size()),
            () -> assertEquals("ACTIVO", menuActualizado.obtEstado()),
            () -> assertTrue(menuActualizado.obtPlatillos().stream()
                    .anyMatch(p -> p.obtNombre().equals("Pollo a la plancha")), 
                    "El platillo principal debería estar presente")
        );
    }
    
    @Test
    @DisplayName("CP-03: Estudiante intenta configurar menú - Acceso denegado")
    void testConfigurarMenu_Estudiante_Denegado() {
        // Arrange - Preparar menú original para comparar después
        Menu menuOriginal = copiarMenu(servicio.obtenerMenu("Desayuno"));
        
        Menu nuevoMenu = new Menu("Desayuno");
        nuevoMenu.setNombre("Desayuno Especial");
        nuevoMenu.setPlatillos(Arrays.asList(
            new Platillo("Plato especial", 5000.0)
        ));
        
        // Act
        servicio.configurarMenu(estudiante, nuevoMenu);
        Menu menuDespues = servicio.obtenerMenu("Desayuno");
        
        // Assert
        assertEquals(menuOriginal.obtNombre(), menuDespues.obtNombre(), 
            "El menú no debería cambiar cuando un estudiante intenta configurarlo");
        assertEquals(menuOriginal.obtPlatillos().size(), menuDespues.obtPlatillos().size(),
            "La cantidad de platillos no debería modificarse");
    }
    
    @Test
    @DisplayName("CP-04: Empleado intenta configurar menú - Acceso denegado")
    void testConfigurarMenu_Empleado_Denegado() {
        // Arrange
        Menu menuOriginal = copiarMenu(servicio.obtenerMenu("Almuerzo"));
        
        Menu nuevoMenu = new Menu("Almuerzo");
        nuevoMenu.setNombre("Almuerzo de Gala");
        nuevoMenu.setPlatillos(Arrays.asList(
            new Platillo("Plato gourmet", 8000.0)
        ));
        
        // Act
        servicio.configurarMenu(empleado, nuevoMenu);
        Menu menuDespues = servicio.obtenerMenu("Almuerzo");
        
        // Assert
        assertEquals(menuOriginal.obtNombre(), menuDespues.obtNombre(),
            "Un empleado no debería poder modificar el menú");
        assertEquals(menuOriginal.obtPlatillos().size(), menuDespues.obtPlatillos().size(),
            "Los platillos no deberían modificarse");
    }
    
    @Test
    @DisplayName("CP-05: Administrador configura menú con lista de platillos null")
    void testConfigurarMenu_PlatillosNull() {
        // Arrange
        Menu nuevoMenu = new Menu("Desayuno");
        nuevoMenu.setNombre("Desayuno sin platos");
        nuevoMenu.setMenuID(102);
        nuevoMenu.setPlatillos(null); // Caso límite: lista null
        
        // Act
        servicio.configurarMenu(administrador, nuevoMenu);
        Menu menuActualizado = servicio.obtenerMenu("Desayuno");
        
        // Assert
        assertAll("Verificar menú con platillos null",
            () -> assertEquals("Desayuno sin platos", menuActualizado.obtNombre(),
                "El nombre debería actualizarse aunque los platillos sean null"),
            () -> assertEquals(102, menuActualizado.obtMenuID(),
                "El ID debería actualizarse"),
            () -> assertNotNull(menuActualizado.obtPlatillos(), 
                "La lista de platillos no debería ser null (debería inicializarse vacía)"),
            () -> assertTrue(menuActualizado.obtPlatillos().isEmpty(),
                "La lista de platillos debería estar vacía")
        );
    }
    
    @Test
    @DisplayName("CP-06: Administrador configura menú con lista de platillos vacía")
    void testConfigurarMenu_PlatillosVacios() {
        // Arrange
        Menu nuevoMenu = new Menu("Almuerzo");
        nuevoMenu.setNombre("Almuerzo sin platos");
        nuevoMenu.setMenuID(202);
        nuevoMenu.setPlatillos(new ArrayList<>()); // Caso límite: lista vacía
        
        // Act
        servicio.configurarMenu(administrador, nuevoMenu);
        Menu menuActualizado = servicio.obtenerMenu("Almuerzo");
        
        // Assert
        assertAll("Verificar menú con platillos vacíos",
            () -> assertEquals("Almuerzo sin platos", menuActualizado.obtNombre()),
            () -> assertEquals(202, menuActualizado.obtMenuID()),
            () -> assertTrue(menuActualizado.obtPlatillos().isEmpty(),
                "La lista de platillos debería estar vacía")
        );
    }
    
    @Test
    @DisplayName("CP-07: Administrador configura menú con un solo platillo")
    void testConfigurarMenu_UnPlatillo() {
        // Arrange
        Menu nuevoMenu = new Menu("Desayuno");
        nuevoMenu.setNombre("Desayuno Mínimo");
        nuevoMenu.setPlatillos(Arrays.asList(
            new Platillo("Café con leche", 1200.0)
        ));
        
        // Act
        servicio.configurarMenu(administrador, nuevoMenu);
        Menu menuActualizado = servicio.obtenerMenu("Desayuno");
        
        // Assert
        assertEquals(1, menuActualizado.obtPlatillos().size(),
            "Debería haber exactamente un platillo");
        assertEquals("Café con leche", 
            menuActualizado.obtPlatillos().get(0).obtNombre(),
            "El nombre del platillo no coincide");
    }
    
    @Test
    @DisplayName("CP-08: Administrador configura menú con muchos platillos")
    void testConfigurarMenu_MuchosPlatillos() {
        // Arrange
        List<Platillo> muchosPlatillos = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            muchosPlatillos.add(new Platillo("Plato " + i, 1000.0 * i));
        }
        
        Menu nuevoMenu = new Menu("Almuerzo");
        nuevoMenu.setNombre("Almuerzo Buffet");
        nuevoMenu.setPlatillos(muchosPlatillos);
        
        // Act
        servicio.configurarMenu(administrador, nuevoMenu);
        Menu menuActualizado = servicio.obtenerMenu("Almuerzo");
        
        // Assert
        assertEquals(20, menuActualizado.obtPlatillos().size(),
            "Deberían mantenerse los 20 platillos");
        assertEquals("Plato 20", 
            menuActualizado.obtPlatillos().get(19).obtNombre(),
            "El último platillo debería ser 'Plato 20'");
    }
}
