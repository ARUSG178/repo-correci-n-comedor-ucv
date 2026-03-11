package com.comedor.modelo.persistencia;

import java.io.IOException;
import java.util.List;

/**
 * Interfaz que define el contrato para el repositorio de códigos de administrador.
 */
public interface IRepositorioAdminCdg {
    
    /**
     * Verifica si un código de administrador es válido.
     * @param codigo El código a verificar
     * @return true si el código es válido, false en caso contrario
     * @throws IOException Si ocurre un error de E/S
     */
    boolean codigoValido(String codigo) throws IOException;
    
    /**
     * Consume (elimina) un código de administrador después de su uso.
     * @param codigo El código a consumir
     * @throws IOException Si ocurre un error de E/S
     */
    void consumirCodigo(String codigo) throws IOException;
    
    /**
     * Obtiene todos los códigos de administrador disponibles.
     * @return Lista de códigos
     * @throws IOException Si ocurre un error de E/S
     */
    List<String> listarCodigos() throws IOException;
}
