package com.comedor.modelo.persistencia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.util.ServicioUtil;

/**
 * Implementación del repositorio de usuarios que persiste en archivo CSV.
 * Aplica el Principio de Responsabilidad Única (SRP) delegando serialización.
 * Aplica el Principio de Inversión de Dependencias (DIP) implementando interfaz.
 */
public class RepoUsuarios implements IRepositorioUsuarios {
    private static final String RUTA_ARCHIVO = "src/main/java/com/comedor/data/usuarios.txt";
    private final UsuarioSerializer serializer;
    
    /**
     * Constructor que permite inyección del serializador.
     * @param serializer El serializador a usar
     */
    public RepoUsuarios(UsuarioSerializer serializer) {
        this.serializer = serializer;
    }
    
    /**
     * Constructor por defecto que crea un serializador interno.
     */
    public RepoUsuarios() {
        this.serializer = new UsuarioSerializer();
    }

    @Override
    public void guardarUsuario(Usuario usuario) throws IOException {
        ServicioUtil.escribirLinea(RUTA_ARCHIVO, serializer.serializar(usuario), true);
    }

    @Override
    public void guardarTodos(List<Usuario> usuarios) throws IOException {
        boolean primeraLinea = true;
        for (Usuario usuario : usuarios) {
            ServicioUtil.escribirLinea(RUTA_ARCHIVO, serializer.serializar(usuario), !primeraLinea);
            primeraLinea = false;
        }
    }

    @Override
    public List<Usuario> listarUsuarios() throws IOException {
        List<Usuario> listaUsuarios = new ArrayList<>();
        List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);
        
        for (String linea : lineas) {
            Usuario u = serializer.deserializar(linea);
            if (u != null) listaUsuarios.add(u);
        }
        return listaUsuarios;
    }
}