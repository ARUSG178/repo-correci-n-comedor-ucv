package com.comedor.modelo.persistencia;

import com.comedor.modelo.entidades.Usuario;
import java.io.IOException;
import java.util.List;

/**
 * Interfaz que define el contrato para el repositorio de usuarios.
 */
public interface IRepositorioUsuarios {
    
    /**
     * Guarda un usuario en el repositorio.
     * @param usuario El usuario a guardar
     * @throws IOException Si ocurre un error de E/S
     */
    void guardarUsuario(Usuario usuario) throws IOException;
    
    /**
     * Obtiene todos los usuarios del repositorio.
     * @return Lista de usuarios registrados
     * @throws IOException Si ocurre un error de E/S
     */
    List<Usuario> listarUsuarios() throws IOException;
    
    /**
     * Sobrescribe el repositorio con la lista completa de usuarios.
     * @param usuarios Lista de usuarios a guardar
     * @throws IOException Si ocurre un error de E/S
     */
    void guardarTodos(List<Usuario> usuarios) throws IOException;
    
    /**
     * Busca un usuario por su cédula.
     * @param cedula La cédula a buscar
     * @return El usuario encontrado o null si no existe
     * @throws IOException Si ocurre un error de E/S
     */
    default Usuario buscarPorCedula(String cedula) throws IOException {
        return listarUsuarios().stream()
            .filter(u -> u != null && u.obtCedula() != null && 
                         u.obtCedula().trim().equalsIgnoreCase(cedula.trim()))
            .findFirst()
            .orElse(null);
    }
}
