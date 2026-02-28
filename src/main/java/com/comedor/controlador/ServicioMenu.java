package com.comedor.controlador;

import java.util.List;

import com.comedor.modelo.entidades.Menu;
import com.comedor.modelo.entidades.Platillo;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Administrador;

public class ServicioMenu {
    private final Menu menuDesayuno = new Menu("Desayuno");
    private final Menu menuAlmuerzo = new Menu("Almuerzo");
    private final ServicioCosto servicioCosto = new ServicioCosto();

    // Factores de cobro según tipo de usuario (1.0 = 100%, 0.2 = 20%)
    private static final double FACTOR_PAGO_ESTUDIANTE = 0.20; // Estudiante paga 20%
    private static final double FACTOR_PAGO_EMPLEADO = 0.50;   // Empleado paga 50%

    // Permite a un administrador actualizar la configuración del menú semanal
    public void configurarMenu(Usuario actor, Menu nuevoMenu) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden modificar el menú.");
            return;
        }

        // Seleccionamos qué menú actualizar según el tipo del nuevo menú
        Menu menu = "Desayuno".equalsIgnoreCase(nuevoMenu.obtTipo()) ? menuDesayuno : menuAlmuerzo;

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
        
        Menu menu = "Desayuno".equalsIgnoreCase(tipoMenu) ? menuDesayuno : menuAlmuerzo;
        // REQUERIMIENTO: Solo un platillo diario. Se limpia la lista antes de agregar.
        menu.obtPlatillos().clear();
        menu.agregarPlatillo(p);
        System.out.println("Platillo agregado a " + menu.obtTipo() + ": " + p.obtNombre());
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
        Menu menu = "Desayuno".equalsIgnoreCase(tipoMenu) ? menuDesayuno : menuAlmuerzo;
        boolean removed = menu.obtPlatillos().removeIf(p -> nombrePlatillo.equalsIgnoreCase(p.obtNombre()));
        if (removed) {
            System.out.println("Platillo eliminado: " + nombrePlatillo);
        } else {
            System.out.println("No se encontró el platillo: " + nombrePlatillo);
        }
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
        Menu menu = "Desayuno".equalsIgnoreCase(tipoMenu) ? menuDesayuno : menuAlmuerzo;
        for (Platillo p : menu.obtPlatillos()) {
            if (nombrePlatillo.equalsIgnoreCase(p.obtNombre())) {
                double anterior = p.obtPrecio();
                servicioCosto.registrarCambioPrecio(p.obtNombre(), anterior, nuevoPrecio, actor.obtCedula());
                p.setPrecio(nuevoPrecio);
                System.out.println("Precio actualizado para " + p.obtNombre() + ": " + nuevoPrecio);
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
        return "Desayuno".equalsIgnoreCase(tipo) ? menuDesayuno : menuAlmuerzo; 
    }
}