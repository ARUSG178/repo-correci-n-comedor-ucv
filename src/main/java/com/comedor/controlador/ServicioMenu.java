package com.comedor.controlador;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.comedor.modelo.entidades.Menu;
import com.comedor.modelo.entidades.Platillo;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Profesor;

public class ServicioMenu {
    private final Menu menuDesayuno = new Menu("Desayuno");
    private final Menu menuAlmuerzo = new Menu("Almuerzo");
    private final ServicioCosto servicioCosto = new ServicioCosto();

    // Factores de cobro según tipo de usuario (1.0 = 100%, 0.2 = 20%)
    private static final double FACTOR_PAGO_ESTUDIANTE = 0.20; // Estudiante paga 20%
    private static final double FACTOR_PAGO_EMPLEADO = 0.50;   // Empleado paga 50%

    public ServicioMenu() {
        cargarMenuDesdeArchivo();
    }

    // Permite a un administrador actualizar la configuración del menú semanal
    public void configurarMenu(Usuario actor, Menu nuevoMenu) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden modificar el menú.");
            return;
        }

        // Seleccionamos qué menú actualizar según el tipo del nuevo menú
        // Se valida explícitamente Desayuno y Almuerzo. Cena no está disponible.
        Menu menu;
        if ("Desayuno".equalsIgnoreCase(nuevoMenu.obtTipo())) {
            menu = menuDesayuno;
        } else if ("Almuerzo".equalsIgnoreCase(nuevoMenu.obtTipo())) {
            menu = menuAlmuerzo;
        } else {
            System.out.println("Error: El servicio '" + nuevoMenu.obtTipo() + "' no está disponible (Solo Desayuno y Almuerzo).");
            return;
        }

        menu.setNombre(nuevoMenu.obtNombre());
        menu.setMenuID(nuevoMenu.obtMenuID());
        menu.setFechaInicio(nuevoMenu.obtFechaInicio());
        menu.setFechaFin(nuevoMenu.obtFechaFin());
        menu.setEstado(nuevoMenu.obtEstado());

        List<Platillo> platillosNuevos = nuevoMenu.obtPlatillos();
        menu.obtPlatillos().clear();
        if (platillosNuevos != null) {
            for (Platillo p : platillosNuevos) {
                menu.agregarPlatillo(p);
            }
        }

        System.out.println("Menú actualizado por administrador (Cédula): " + actor.obtCedula());
        guardarMenuEnArchivo();
    }

    // Agrega un nuevo platillo al menú actual si el usuario es administrador
    public void agregarPlatillo(Usuario actor, Platillo p, String tipoMenu) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden agregar platillos.");
            return;
        }
        if (p == null) {
            System.out.println("Platillo nulo.");
            return;
        }
        
        Menu menu;
        if ("Desayuno".equalsIgnoreCase(tipoMenu)) {
            menu = menuDesayuno;
        } else if ("Almuerzo".equalsIgnoreCase(tipoMenu)) {
            menu = menuAlmuerzo;
        } else {
            System.out.println("Error: Servicio no disponible para agregar platillo: " + tipoMenu);
            return;
        }

        // REQUERIMIENTO: Solo un platillo diario. Se limpia la lista antes de agregar.
        menu.obtPlatillos().clear();
        menu.agregarPlatillo(p);
        System.out.println("Platillo agregado a " + menu.obtTipo() + ": " + p.obtNombre());
        guardarMenuEnArchivo();
    }

    // Elimina un platillo del menú por su nombre si el usuario es administrador
    public void quitarPlatillo(Usuario actor, String nombrePlatillo, String tipoMenu) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden quitar platillos.");
            return;
        }
        if (nombrePlatillo == null || nombrePlatillo.isEmpty()) {
            System.out.println("Nombre de platillo inválido.");
            return;
        }
        Menu menu;
        if ("Desayuno".equalsIgnoreCase(tipoMenu)) {
            menu = menuDesayuno;
        } else if ("Almuerzo".equalsIgnoreCase(tipoMenu)) {
            menu = menuAlmuerzo;
        } else {
            System.out.println("Error: Servicio no disponible para quitar platillo: " + tipoMenu);
            return;
        }

        boolean removed = menu.obtPlatillos().removeIf(p -> nombrePlatillo.equalsIgnoreCase(p.obtNombre()));
        if (removed) {
            System.out.println("Platillo eliminado: " + nombrePlatillo);
        } else {
            System.out.println("No se encontró el platillo: " + nombrePlatillo);
        }
        guardarMenuEnArchivo();
    }

    // Actualiza el precio de un platillo y registra el cambio en costos si el usuario es administrador
    public void actualizarPrecioPlatillo(Usuario actor, String nombrePlatillo, double nuevoPrecio, String tipoMenu) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden actualizar precios.");
            return;
        }
        if (nombrePlatillo == null || nombrePlatillo.isEmpty()) {
            System.out.println("Nombre de platillo inválido.");
            return;
        }
        Menu menu;
        if ("Desayuno".equalsIgnoreCase(tipoMenu)) {
            menu = menuDesayuno;
        } else if ("Almuerzo".equalsIgnoreCase(tipoMenu)) {
            menu = menuAlmuerzo;
        } else {
            System.out.println("Error: Servicio no disponible para actualizar precio: " + tipoMenu);
            return;
        }

        for (Platillo p : menu.obtPlatillos()) {
            if (nombrePlatillo.equalsIgnoreCase(p.obtNombre())) {
                double anterior = p.obtPrecio();
                servicioCosto.registrarCambioPrecio(p.obtNombre(), anterior, nuevoPrecio, actor.obtCedula());
                p.setPrecio(nuevoPrecio);
                System.out.println("Precio actualizado para " + p.obtNombre() + ": " + nuevoPrecio);
                guardarMenuEnArchivo();
                return;
            }
        }
        System.out.println("No se encontró el platillo: " + nombrePlatillo);
    }

    // Permite al administrador registrar los costos fijos, variables y producción del mes para el cálculo del CCB
    public void registrarCostosMensuales(Usuario actor, double fijos, double variables, int produccion) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden registrar costos.");
            return;
        }
        // Se elimina la llamada redundante a registrarValoresCCB porque calcularRegistrarCCBCompleto ya lo hace internamente
        // Se asume 0 merma para este registro manual simplificado
        servicioCosto.calcularRegistrarCCBCompleto(fijos, variables, produccion, 0);
        System.out.println("Costos mensuales actualizados por: " + actor.obtCedula());
        guardarMenuEnArchivo();
    }

    // Calcula la tarifa automática basada en el rol del usuario y el precio base del platillo.
    public double calcularTarifaPorUsuario(Usuario usuario, Platillo p) {
        // 1. Obtenemos el CCB real del periodo. Si es 0 (no hay datos), usamos el precio referencial del platillo.
        double ccb = servicioCosto.obtenerCCBActual();
        // Corrección: Eliminada declaración duplicada y uso de método correcto obtPrecio()
        double precioBase = (ccb > 0) ? ccb : p.obtPrecio();
        
        if (usuario instanceof Estudiante) {
            return precioBase * FACTOR_PAGO_ESTUDIANTE;
        } else if (usuario instanceof Empleado) {
            return precioBase * FACTOR_PAGO_EMPLEADO;
        } else if (usuario instanceof Profesor) {
            return precioBase; // Profesores pagan 100%
        } else {
            return precioBase; // Administradores o externos pagan completo
        }
    }

    // Muestra en consola la información del menú actual y sus platillos
    public void visualizarMenu(Usuario actor) {
        visualizarMenuIndividual(menuDesayuno);
        System.out.println("-------------------------");
        visualizarMenuIndividual(menuAlmuerzo);
    }

    private void visualizarMenuIndividual(Menu m) {
        if (m.obtPlatillos() == null || m.obtPlatillos().isEmpty()) {
            System.out.println("Menú " + m.obtTipo() + ": No configurado.");
            return;
        }
        System.out.println("Menú " + m.obtTipo() + ": " + (m.obtNombre() != null ? m.obtNombre() : "(sin nombre)"));
        System.out.println("Periodo: " + m.obtFechaInicio() + " - " + m.obtFechaFin());
        System.out.println("Platillos:");
        for (Platillo p : m.obtPlatillos()) {
            System.out.println(" - " + p);
        }
    }

    // Retorna la instancia del menú solicitado
    public Menu obtenerMenu(String tipo) { 
        if ("Desayuno".equalsIgnoreCase(tipo)) {
            return menuDesayuno;
        } else if ("Almuerzo".equalsIgnoreCase(tipo)) {
            return menuAlmuerzo;
        }
        return null; // Cena u otros no disponibles
    }

    // --- PERSISTENCIA DE DATOS ---

    private void guardarMenuEnArchivo() {
        Properties props = new Properties();
        
        // Guardar Desayuno
        if (!menuDesayuno.obtPlatillos().isEmpty()) {
            Platillo p = menuDesayuno.obtPlatillos().get(0);
            props.setProperty("desayuno_nombre", p.obtNombre());
            props.setProperty("desayuno_precio", String.valueOf(p.obtPrecio()));
            props.setProperty("desayuno_imagen", p.obtImagen() != null ? p.obtImagen() : "");
            props.setProperty("desayuno_descripcion", p.obtDescripcion() != null ? p.obtDescripcion() : "");
            props.setProperty("desayuno_nutricion", p.obtInfoNutricional() != null ? p.obtInfoNutricional() : "");
        }

        // Guardar Almuerzo
        if (!menuAlmuerzo.obtPlatillos().isEmpty()) {
            Platillo p = menuAlmuerzo.obtPlatillos().get(0);
            props.setProperty("almuerzo_nombre", p.obtNombre());
            props.setProperty("almuerzo_precio", String.valueOf(p.obtPrecio()));
            props.setProperty("almuerzo_imagen", p.obtImagen() != null ? p.obtImagen() : "");
            props.setProperty("almuerzo_descripcion", p.obtDescripcion() != null ? p.obtDescripcion() : "");
            props.setProperty("almuerzo_nutricion", p.obtInfoNutricional() != null ? p.obtInfoNutricional() : "");
        }

        // Guardar CCB
        props.setProperty("ccb_actual", String.valueOf(servicioCosto.obtenerCCBActual()));

        try (FileOutputStream out = new FileOutputStream("menu_config.properties")) {
            props.store(out, "Configuracion del Menu - SAGC");
        } catch (IOException e) {
            System.err.println("Error guardando configuración del menú: " + e.getMessage());
        }
    }

    private void cargarMenuDesdeArchivo() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("menu_config.properties")) {
            props.load(in);
            
            cargarPlatilloEnMenu(menuDesayuno, props, "desayuno");
            cargarPlatilloEnMenu(menuAlmuerzo, props, "almuerzo");
            
        } catch (IOException e) {
            // Archivo no existe, se inicia vacío
        }
    }

    private void cargarPlatilloEnMenu(Menu menu, Properties props, String prefijo) {
        String nombre = props.getProperty(prefijo + "_nombre");
        if (nombre != null && !nombre.isEmpty()) {
            double precio = Double.parseDouble(props.getProperty(prefijo + "_precio", "0.0"));
            String imagen = props.getProperty(prefijo + "_imagen", "");
            String desc = props.getProperty(prefijo + "_descripcion", "");
            String nutri = props.getProperty(prefijo + "_nutricion", "");
            
            Platillo p = new Platillo(nombre, desc, precio, imagen, nutri);
            menu.obtPlatillos().clear();
            menu.agregarPlatillo(p);
        }
    }
}