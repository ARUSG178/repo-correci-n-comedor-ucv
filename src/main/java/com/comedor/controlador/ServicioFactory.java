package com.comedor.controlador;

import com.comedor.modelo.persistencia.*;

/**
 * Centraliza la creación de instancias.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ServicioFactory {
    private static ServicioFactory instance;
    
    // Repositorios compartidos
    private final IRepositorioUsuarios repoUsuarios;
    private final IRepositorioSecretaria repoSecretaria;
    private final IRepositorioAdminCdg repoAdminCdg;
    
    private ServicioFactory() {
        // Inicializar repositorios concretos
        this.repoUsuarios = new RepoUsuarios();
        this.repoSecretaria = new RepoSecretaria();
        this.repoAdminCdg = new RepoAdminCdg();
    }
    
    /**
     * Obtiene la instancia única de la fábrica.
     * @return La instancia de ServicioFactory
     */
    public static synchronized ServicioFactory getInstance() {
        if (instance == null) {
            instance = new ServicioFactory();
        }
        return instance;
    }
    
    /**
     * Crea un servicio de inicio de sesión con las dependencias inyectadas.
     * @return Una nueva instancia de ServicioIS
     */
    public ServicioIS crearServicioIS() {
        return new ServicioIS(repoUsuarios);
    }
    
    /**
     * Crea un servicio de registro con las dependencias inyectadas.
     * @return Una nueva instancia de ServicioRegistro
     */
    public ServicioRegistro crearServicioRegistro() {
        return new ServicioRegistro(repoUsuarios, repoSecretaria, repoAdminCdg);
    }
    
    /**
     * Obtiene el repositorio de usuarios.
     * @return El repositorio de usuarios
     */
    public IRepositorioUsuarios getRepositorioUsuarios() {
        return repoUsuarios;
    }
    
    /**
     * Obtiene el repositorio de secretaria.
     * @return El repositorio de secretaria
     */
    public IRepositorioSecretaria getRepositorioSecretaria() {
        return repoSecretaria;
    }
    
    /**
     * Obtiene el repositorio de códigos de administrador.
     * @return El repositorio de códigos admin
     */
    public IRepositorioAdminCdg getRepositorioAdminCdg() {
        return repoAdminCdg;
    }
}
