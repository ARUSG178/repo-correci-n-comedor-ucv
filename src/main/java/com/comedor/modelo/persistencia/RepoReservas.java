package com.comedor.modelo.persistencia;

import com.comedor.modelo.entidades.Reserva;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.util.ServicioUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RepoReservas {
    private static final String RUTA_ARCHIVO = "src/main/java/com/comedor/data/reservas.txt";
    private static List<Reserva> reservas = new ArrayList<>();
    
    // Cargar reservas desde archivo al iniciar
    static {
        cargarReservasDesdeArchivo();
    }
    
    private static void cargarReservasDesdeArchivo() {
        try {
            List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);
            for (String linea : lineas) {
                if (!linea.trim().isEmpty()) {
                    Reserva reserva = deserializarReserva(linea);
                    if (reserva != null) {
                        reservas.add(reserva);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando reservas desde archivo: " + e.getMessage());
        }
    }
    
    private static void guardarReservasEnArchivo() {
        try {
            // Limpiar archivo
            ServicioUtil.escribirLinea(RUTA_ARCHIVO, "", false);
            
            // Escribir todas las reservas
            boolean primeraLinea = true;
            for (Reserva reserva : reservas) {
                ServicioUtil.escribirLinea(RUTA_ARCHIVO, serializarReserva(reserva), primeraLinea);
                primeraLinea = false;
            }
        } catch (Exception e) {
            System.err.println("Error guardando reservas en archivo: " + e.getMessage());
        }
    }
    
    private static String serializarReserva(Reserva reserva) {
        return String.format("%s;%s;%s", 
            reserva.obtPropietario().obtCedula(),
            reserva.obtHorarioReservado().toString(),
            reserva.obtEstado());
    }
    
    private static Reserva deserializarReserva(String linea) {
        try {
            String[] datos = linea.split(";");
            if (datos.length >= 3) {
                String cedula = datos[0].trim();
                LocalDateTime horario = LocalDateTime.parse(datos[1].trim());
                String estado = datos[2].trim();
                
                // Crear usuario temporal (solo para la reserva)
                Usuario usuario = new Usuario(cedula, "") {
                    @Override public double calcularTarifa(double precioBase) { return precioBase; }
                    @Override public String obtTipo() { return "Usuario"; }
                    @Override public boolean obtEstado() { return true; }
                    @Override public int obtIntentosFallidos() { return 0; }
                    @Override public double obtSaldo() { return 0.0; }
                    @Override public void setNombre(String nombre) {}
                    @Override public void setEstado(boolean estado) {}
                    @Override public void setSaldo(double saldo) {}
                    @Override public void setIntentosFallidos(int intentos) {}
                };
                usuario.setNombre("Usuario Reserva");
                
                return new Reserva(usuario, horario, estado);
            }
        } catch (Exception e) {
            System.err.println("Error deserializando reserva: " + e.getMessage());
        }
        return null;
    }
    
    public static void guardarReserva(Reserva reserva) {
        reservas.add(reserva);
        guardarReservasEnArchivo(); // Persistir inmediatamente
    }
    
    public static List<Reserva> obtenerReservasPorUsuario(Usuario usuario) {
        return reservas.stream()
                .filter(r -> r.obtPropietario().obtCedula().equals(usuario.obtCedula()))
                .collect(Collectors.toList());
    }
    
    public static List<Reserva> obtenerTodasLasReservas() {
        return new ArrayList<>(reservas);
    }
    
    // Verifica si el usuario ya tiene una reserva para el mismo día y turno (desayuno/almuerzo)
    public static boolean existeReservaDelMismoTipo(Usuario usuario, LocalDateTime fechaHora) {
        LocalDate fecha = fechaHora.toLocalDate();
        int hora = fechaHora.getHour();
        
        return reservas.stream()
                .filter(r -> r.obtPropietario().obtCedula().equals(usuario.obtCedula()))
                .filter(r -> r.obtHorarioReservado().toLocalDate().equals(fecha))
                .anyMatch(r -> {
                    int horaExistente = r.obtHorarioReservado().getHour();
                    // Desayuno: 7-11, Almuerzo: 12-16 (4pm)
                    boolean esDesayunoNuevo = hora >= 7 && hora < 12;
                    boolean esAlmuerzoNuevo = hora >= 12 && hora < 17;
                    boolean esDesayunoExistente = horaExistente >= 7 && horaExistente < 12;
                    boolean esAlmuerzoExistente = horaExistente >= 12 && horaExistente < 17;
                    
                    return (esDesayunoNuevo && esDesayunoExistente) || (esAlmuerzoNuevo && esAlmuerzoExistente);
                });
    }
}
