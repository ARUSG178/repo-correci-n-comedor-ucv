package com.comedor.modelo.persistencia;

import com.comedor.modelo.entidades.Reserva;
import com.comedor.modelo.entidades.Usuario;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RepoReservas {
    private static List<Reserva> reservas = new ArrayList<>();
    
    public static void guardarReserva(Reserva reserva) {
        reservas.add(reserva);
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
