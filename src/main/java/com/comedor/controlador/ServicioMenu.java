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
import com.comedor.modelo.entidades.EstudianteBecario;
import com.comedor.modelo.entidades.EstudianteExonerado;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Profesor;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.utilidades.Logger;

public class ServicioMenu {
    private final Menu menuDesayuno = new Menu("Desayuno");
    private final Menu menuAlmuerzo = new Menu("Almuerzo");
    private final ServicioCosto servicioCosto = new ServicioCosto();

    private double parseFactor(String pct, double fallback) {
        if (pct == null || pct.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Double.parseDouble(pct.trim()) / 100.0;
        } catch (Exception e) {
            return fallback;
        }
    }

    public double factorParaUsuario(Usuario usuario) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("menu_config.properties")) {
            props.load(in);
        } catch (Exception e) {
            // Sin config
        }

        if (usuario instanceof EstudianteExonerado) {
            return 0.0;
        }
        if (usuario instanceof EstudianteBecario) {
            return 0.05;
        }
        if (usuario instanceof Estudiante) {
            return parseFactor(props.getProperty("tarifa_pct_estudiante"), 0.20);
        }
        if (usuario instanceof Empleado) {
            return parseFactor(props.getProperty("tarifa_pct_empleado"), 1.00);
        }
        if (usuario instanceof Profesor) {
            return parseFactor(props.getProperty("tarifa_pct_profesor"), 1.00);
        }
        return 1.00;
    }

    public ServicioMenu() {
        cargarMenuDesdeArchivo();
    }

    // Permite a un administrador actualizar la configuración del menú semanal
    public void configurarMenu(Usuario actor, Menu nuevoMenu) {
        if (!(actor instanceof Administrador)) {
            Logger.warning("Acceso denegado: solo administradores pueden modificar el menú.");
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
            Logger.warning("Error: El servicio '" + nuevoMenu.obtTipo() + "' no está disponible (Solo Desayuno y Almuerzo).");
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

        Logger.info("Menú actualizado por administrador (Cédula): " + actor.obtCedula());
        guardarMenuEnArchivo();
    }

    // Agrega un nuevo platillo al menú actual si el usuario es administrador
    public void agregarPlatillo(Usuario actor, Platillo p, String tipoMenu) {
        if (!(actor instanceof Administrador)) {
            Logger.warning("Acceso denegado: solo administradores pueden agregar platillos.");
            return;
        }
        if (p == null) {
            Logger.warning("Platillo nulo.");
            return;
        }
        
        Menu menu;
        if ("Desayuno".equalsIgnoreCase(tipoMenu)) {
            menu = menuDesayuno;
        } else if ("Almuerzo".equalsIgnoreCase(tipoMenu)) {
            menu = menuAlmuerzo;
        } else {
            Logger.warning("Error: Servicio no disponible para agregar platillo: " + tipoMenu);
            return;
        }

        // REQUERIMIENTO: Solo un platillo diario. Se limpia la lista antes de agregar.
        menu.obtPlatillos().clear();
        menu.agregarPlatillo(p);
        Logger.info("Platillo agregado a " + menu.obtTipo() + ": " + p.obtNombre());
        guardarMenuEnArchivo();
    }

    // Elimina un platillo del menú por su nombre si el usuario es administrador
    public void quitarPlatillo(Usuario actor, String nombrePlatillo, String tipoMenu) {
        if (!(actor instanceof Administrador)) {
            Logger.warning("Acceso denegado: solo administradores pueden quitar platillos.");
            return;
        }
        if (nombrePlatillo == null || nombrePlatillo.isEmpty()) {
            Logger.warning("Nombre de platillo inválido.");
            return;
        }
        Menu menu;
        if ("Desayuno".equalsIgnoreCase(tipoMenu)) {
            menu = menuDesayuno;
        } else if ("Almuerzo".equalsIgnoreCase(tipoMenu)) {
            menu = menuAlmuerzo;
        } else {
            Logger.warning("Error: Servicio no disponible para quitar platillo: " + tipoMenu);
            return;
        }

        boolean removed = menu.obtPlatillos().removeIf(p -> nombrePlatillo.equalsIgnoreCase(p.obtNombre()));
        if (removed) {
            Logger.info("Platillo eliminado: " + nombrePlatillo);
        } else {
            Logger.warning("No se encontró el platillo: " + nombrePlatillo);
        }
        guardarMenuEnArchivo();
    }

    // Actualiza el precio de un platillo y registra el cambio en costos si el usuario es administrador
    public void actualizarPrecioPlatillo(Usuario actor, String nombrePlatillo, double nuevoPrecio, String tipoMenu) {
        if (!(actor instanceof Administrador)) {
            Logger.warning("Acceso denegado: solo administradores pueden actualizar precios.");
            return;
        }
        if (nombrePlatillo == null || nombrePlatillo.isEmpty()) {
            Logger.warning("Nombre de platillo inválido.");
            return;
        }
        Menu menu;
        if ("Desayuno".equalsIgnoreCase(tipoMenu)) {
            menu = menuDesayuno;
        } else if ("Almuerzo".equalsIgnoreCase(tipoMenu)) {
            menu = menuAlmuerzo;
        } else {
            Logger.warning("Error: Servicio no disponible para actualizar precio: " + tipoMenu);
            return;
        }

        for (Platillo p : menu.obtPlatillos()) {
            if (nombrePlatillo.equalsIgnoreCase(p.obtNombre())) {
                double anterior = p.obtPrecio();
                servicioCosto.registrarCambioPrecio(p.obtNombre(), anterior, nuevoPrecio, actor.obtCedula());
                p.setPrecio(nuevoPrecio);
                Logger.info("Precio actualizado para " + p.obtNombre() + ": " + nuevoPrecio);
                guardarMenuEnArchivo();
                return;
            }
        }
        Logger.warning("No se encontró el platillo: " + nombrePlatillo);
    }

    // Permite al administrador registrar los costos fijos, variables y producción del mes para el cálculo del CCB
    public void registrarCostosMensuales(Usuario actor, double fijos, double variables, int produccion) {
        if (!(actor instanceof Administrador)) {
            Logger.warning("Acceso denegado: solo administradores pueden registrar costos.");
            return;
        }
        // Se elimina la llamada redundante a registrarValoresCCB porque calcularRegistrarCCBCompleto ya lo hace internamente
        // Se asume 0 merma para este registro manual simplificado
        servicioCosto.calcularRegistrarCCBCompleto(fijos, variables, produccion, 0);
        Logger.info("Costos mensuales actualizados por: " + actor.obtCedula());
        guardarMenuEnArchivo();
    }

    // Calcula la tarifa automática basada en el rol del usuario y el precio base del platillo.
    public double calcularTarifaPorUsuario(Usuario usuario, Platillo p) {
        // 1. Obtenemos el CCB real del periodo. Si es 0 (no hay datos), usamos el precio referencial del platillo.
        double ccb = servicioCosto.obtenerCCBActual();
        // Corrección: Eliminada declaración duplicada y uso de método correcto obtPrecio()
        double precioBase = (ccb > 0) ? ccb : p.obtPrecio();
        
        return precioBase * factorParaUsuario(usuario);
    }

    // Muestra en consola la información del menú actual y sus platillos
    public void visualizarMenu(Usuario actor) {
        visualizarMenuIndividual(menuDesayuno);
        Logger.info("-------------------------");
        visualizarMenuIndividual(menuAlmuerzo);
    }

    private void visualizarMenuIndividual(Menu m) {
        if (m.obtPlatillos() == null || m.obtPlatillos().isEmpty()) {
            Logger.info("Menú " + m.obtTipo() + ": No configurado.");
            return;
        }
        Logger.info("Menú " + m.obtTipo() + ": " + (m.obtNombre() != null ? m.obtNombre() : "(sin nombre)"));
        Logger.info("Periodo: " + m.obtFechaInicio() + " - " + m.obtFechaFin());
        Logger.info("Platillos:");
        for (Platillo p : m.obtPlatillos()) {
            Logger.info(" - " + p);
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
            String precioStr = props.getProperty(prefijo + "_precio", "0.0");
            // Limpieza: Reemplazar comas por puntos y eliminar símbolos (como $)
            precioStr = precioStr.replace(",", ".");
            precioStr = precioStr.replaceAll("[^\\d.]", "");
            
            double precio = 0.0;
            try {
                if (!precioStr.isEmpty()) precio = Double.parseDouble(precioStr);
            } catch (NumberFormatException e) {
                Logger.warning("Error formato precio en config (" + prefijo + "): " + precioStr);
            }
            
            String imagen = props.getProperty(prefijo + "_imagen", "");
            String desc = props.getProperty(prefijo + "_descripcion", "");
            String nutri = props.getProperty(prefijo + "_nutricion", "");
            
            Platillo p = new Platillo(nombre, desc, precio, imagen, nutri);
            menu.obtPlatillos().clear();
            menu.agregarPlatillo(p);
        }
    }
}