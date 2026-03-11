package com.comedor.modelo.persistencia;

import com.comedor.modelo.entidades.Usuario;
import java.io.IOException;

/**
 * Interfaz que define el contrato para el repositorio de datos de secretaría.
 */
public interface IRepositorioSecretaria {
    
    /**
     * Busca un registro en la base de datos de la UCV por cédula.
     * @param cedulaBuscada La cédula a buscar
     * @return El usuario con sus datos oficiales o null si no se encuentra
     * @throws IOException Si ocurre un error de E/S
     */
    Usuario buscarRegistroUCV(String cedulaBuscada) throws IOException;
}
