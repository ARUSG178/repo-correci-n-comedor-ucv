package com.comedor.controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.RegistroCosto;
import com.comedor.modelo.persistencia.RepoUsuarios;
import com.comedor.util.ServicioUtil;
import com.comedor.utilidades.Logger;

public class ServicioCosto {
    private List<RegistroCosto> costos = new ArrayList<>();
    private Map<String, Integer> produccionBandejas = new HashMap<>();
    private Map<String, Double> mermas = new HashMap<>();
    private double ccbActual = 0.0; // Almacena el último CCB calculado

    // Registra la cantidad estimada de bandejas a producir en un periodo específico
    public void registrarProduccionBandejas(String periodo, int cantidad) {
        produccionBandejas.put(periodo, cantidad);
        Logger.info("Producción estimada de bandejas para " + periodo + ": " + cantidad);
    }

    // Registra el porcentaje de merma (0.0 a 1.0) esperado para el periodo
    public void registrarMerma(String periodo, double porcentaje) {
        mermas.put(periodo, porcentaje);
        Logger.info("Merma registrada para " + periodo + ": " + (porcentaje * 100) + "%");
    }

    // Agrega un nuevo registro de costo al sistema con su tipo y descripción
    public void agregarCosto(String periodo, RegistroCosto.TipoCosto tipo, String descripcion, double monto) {
        costos.add(new RegistroCosto(periodo, tipo, descripcion, monto));
        Logger.info("Costo agregado: " + descripcion + " (" + tipo + ")");
    }

    // Obtiene una lista de costos filtrados por el periodo especificado
    public List<RegistroCosto> obtenerCostosPorPeriodo(String periodo) {
        return costos.stream().filter(c -> c.obtPeriodo().equals(periodo)).collect(Collectors.toList());
    }
    
    // Calcula la suma total de los montos de costos para un periodo dado
    public double obtenerTotalCostosPorPeriodo(String periodo) {
        return obtenerCostosPorPeriodo(periodo).stream().mapToDouble(RegistroCosto::obtMonto).sum();
    }

    // Calcula el costo unitario por bandeja dividiendo el total de costos entre la producción
    public double calcularCostoUnitarioBandeja(String periodo) {
        double totalCostos = obtenerTotalCostosPorPeriodo(periodo);
        int cantidad = produccionBandejas.getOrDefault(periodo, 0);
        if (cantidad <= 0) {
            return 0.0;
        }
        return totalCostos / cantidad;
    }

    // Calcula el Costo Base de Bandeja (CCB) considerando producción, costos y merma
    public double calcularCCB(String periodo) {
        double totalCostos = obtenerTotalCostosPorPeriodo(periodo);
        int cantidadPlanificada = produccionBandejas.getOrDefault(periodo, 0);
        double porcentajeMerma = mermas.getOrDefault(periodo, 0.0);

        if (cantidadPlanificada <= 0) return 0.0;

        // El CCB se calcula agregando el porcentaje de merma al costo unitario base
        double costoUnitario = totalCostos / cantidadPlanificada;
        this.ccbActual = costoUnitario * (1 + porcentajeMerma);
        return this.ccbActual;
    }

    // Registra un cambio de precio de platillo como costo variable en el periodo actual
    public void registrarCambioPrecio(String nombrePlatillo, double precioAnterior, double precioNuevo, String actor) {
        String periodo = ServicioUtil.obtenerPeriodoActual();
        double diferencia = precioNuevo - precioAnterior;
        String descripcion = String.format("Cambio precio %s por %s: %.2f -> %.2f (diff %.2f)", nombrePlatillo, actor, precioAnterior, precioNuevo, diferencia);
        agregarCosto(periodo, RegistroCosto.TipoCosto.VARIABLE, descripcion, precioNuevo);
        Logger.info("Registro de cambio de precio creado: " + descripcion);
    }

    // --- MÉTODOS AGREGADOS PARA CUMPLIR CON REQUERIMIENTOS DE SERVICIOMENU ---

    // Retorna el último CCB calculado para aplicar tarifas
    public double obtenerCCBActual() {
        return this.ccbActual;
    }

    // Método helper para registrar todo en un solo paso (usado por ServicioMenu)
    public void registrarValoresCCB(String periodo, double fijos, double variables, int produccion) {
        agregarCosto(periodo, RegistroCosto.TipoCosto.FIJO, "Costos Fijos Mensuales", fijos);
        agregarCosto(periodo, RegistroCosto.TipoCosto.VARIABLE, "Costos Variables Mensuales", variables);
        registrarProduccionBandejas(periodo, produccion);
    }

    // Calcula y guarda el CCB completo recibiendo el porcentaje de merma directo (0.0 a 1.0)
    public double calcularRegistrarCCBCompleto(double fijos, double variables, int produccion, double porcentajeMerma) {
        String periodo = ServicioUtil.obtenerPeriodoActual();
        registrarValoresCCB(periodo, fijos, variables, produccion);
        registrarMerma(periodo, porcentajeMerma);
        return calcularCCB(periodo);
    }

    public String obtenerPeriodoActual() {
        return ServicioUtil.obtenerPeriodoActual();
    }

    // Procesa la recarga de saldo validando los datos y persistiendo el cambio
    public void procesarRecarga(Usuario usuario, double monto, String banco, String referencia) throws Exception {
        procesarRecarga(usuario, monto, banco, referencia, null);
    }

    public void procesarRecarga(Usuario usuario, double monto, String banco, String referencia, String cedulaDestino) throws Exception {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser mayor a 0.");
        }

        boolean esSaldoPana = cedulaDestino != null && !cedulaDestino.trim().isEmpty();
        if (!esSaldoPana) {
            if (banco == null || banco.trim().isEmpty()) {
                throw new IllegalArgumentException("Debe seleccionar un banco de procedencia.");
            }
            if (referencia == null || referencia.trim().isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar el número de referencia de la transacción.");
            }
        }

        // VALIDACIÓN DE TIPO DE USUARIO PARA SALDO PANA
        if (esSaldoPana) {
            validarSaldoPana(usuario, cedulaDestino);
            validarSaldoSuficiente(usuario, monto); // Validar que tiene saldo suficiente
        }

        String destino = esSaldoPana ? cedulaDestino.trim() : usuario.obtCedula();

        RepoUsuarios repo = new RepoUsuarios();
        List<Usuario> usuarios = repo.listarUsuarios();
        boolean encontradoDestino = false;
        boolean saldoOrigenActualizado = !esSaldoPana; // Si no es saldo pana, no hay que actualizar origen

        for (Usuario u : usuarios) {
            // Actualizar saldo del usuario destino
            if (u.obtCedula().equals(destino)) {
                double nuevoSaldoDestino = u.obtSaldo() + monto;
                u.setSaldo(nuevoSaldoDestino);
                encontradoDestino = true;
            }
            
            // Si es Saldo Pana, descontar saldo del usuario origen
            if (esSaldoPana && u.obtCedula().equals(usuario.obtCedula())) {
                double nuevoSaldoOrigen = u.obtSaldo() - monto;
                u.setSaldo(nuevoSaldoOrigen);
                usuario.setSaldo(nuevoSaldoOrigen); // Actualizar objeto en memoria
                saldoOrigenActualizado = true;
            }
        }

        if (!encontradoDestino) {
            throw new IOException("No se encontró el usuario destino en la base de datos para actualizar el saldo.");
        }
        
        if (!saldoOrigenActualizado) {
            throw new IOException("No se encontró el usuario origen en la base de datos para descontar el saldo.");
        }

        repo.guardarTodos(usuarios);
    }
    
    /**
     * Valida que solo los estudiantes puedan usar Saldo Pana.
     * @param usuarioOrigen Usuario que intenta hacer la recarga
     * @param cedulaDestino Cédula del usuario destino
     * @throws IllegalArgumentException Si la validación falla
     */
    private void validarSaldoPana(Usuario usuarioOrigen, String cedulaDestino) throws Exception {
        // 0. Verificar que no se recargue a sí mismo
        if (usuarioOrigen.obtCedula().equals(cedulaDestino.trim())) {
            throw new IllegalArgumentException("No puedes recargarte saldo a ti mismo usando Saldo Pana.");
        }
        
        // 1. El usuario origen debe ser algún tipo de estudiante
        if (!esEstudiante(usuarioOrigen)) {
            throw new IllegalArgumentException("Solo los estudiantes (regulares, becarios o exonerados) pueden usar Saldo Pana para recargar a otros comensales.");
        }
        
        // 2. Buscar el usuario destino para validar que sea estudiante
        RepoUsuarios repo = new RepoUsuarios();
        List<Usuario> usuarios = repo.listarUsuarios();
        
        for (Usuario u : usuarios) {
            if (u.obtCedula().equals(cedulaDestino.trim())) {
                // 3. El usuario destino también debe ser algún tipo de estudiante
                if (!esEstudiante(u)) {
                    throw new IllegalArgumentException("Solo se puede recargar saldo a estudiantes. El usuario destino es de tipo: " + u.obtTipo());
                }
                return; // Validación exitosa
            }
        }
        
        throw new IllegalArgumentException("No se encontró el usuario destino en el sistema.");
    }
    
    /**
     * Verifica si un usuario es algún tipo de estudiante.
     * @param usuario Usuario a verificar
     * @return true si es estudiante (regular, becario o exonerado)
     */
    private boolean esEstudiante(Usuario usuario) {
        String tipo = usuario.obtTipo();
        return tipo.equals("Estudiante") || 
               tipo.equals("EstudianteBecario") || 
               tipo.equals("EstudianteExonerado");
    }
    
    /**
     * Valida que el usuario tenga saldo suficiente para realizar la transferencia.
     * @param usuario Usuario que envía el saldo
     * @param monto Monto a transferir
     * @throws IllegalArgumentException Si no tiene saldo suficiente
     */
    private void validarSaldoSuficiente(Usuario usuario, double monto) {
        if (usuario.obtSaldo() < monto) {
            throw new IllegalArgumentException(
                String.format("Saldo insuficiente. Tienes $%.2f pero necesitas $%.2f para esta transferencia.", 
                    usuario.obtSaldo(), monto)
            );
        }
    }
}