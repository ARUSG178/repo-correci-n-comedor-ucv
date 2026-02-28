package com.comedor.modelo.entidades;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reserva {
    private Usuario propietario;
    private LocalDateTime horarioReservado;
    private String estado;
    private String clave_acceso;

    // Crea una nueva reserva con propietario, horario y estado, generando una clave única
    public Reserva(Usuario propietario, LocalDateTime horarioReservado, String estado) {
        this.propietario = propietario;
        this.horarioReservado = horarioReservado;
        this.estado = estado;
        this.clave_acceso = genClaveAcceso();
    }

    // Genera una clave de acceso aleatoria de 12 caracteres
    String genClaveAcceso() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    // Retorna el usuario que realizó la reserva
    public Usuario obtPropietario() { return propietario; }
    
    // Retorna la fecha y hora reservada
    public LocalDateTime obtHorarioReservado() { return horarioReservado; }
    
    // Retorna el estado actual de la reserva
    public String obtEstado() { return estado; }
    
    // Retorna la clave de acceso generada para la reserva
    public String obtClaveAcceso() { return clave_acceso; }

    // Retorna una representación en texto de la reserva
    @Override
    public String toString() {
        return "Reserva de " + propietario.obtCedula() + " en " + horarioReservado +  " [estado ->" + estado + ", clave ->" + clave_acceso + "]";
    }
}