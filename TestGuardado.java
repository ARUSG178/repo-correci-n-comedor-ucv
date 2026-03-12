import java.io.IOException;
import com.comedor.modelo.entidades.*;
import com.comedor.modelo.persistencia.RepoSecretaria;

public class TestGuardado {
    public static void main(String[] args) {
        try {
            RepoSecretaria repo = new RepoSecretaria();
            
            // Crear un usuario de prueba
            Administrador admin = new Administrador("99999999", "test123", "ADM-999");
            admin.setNombre("Usuario de Prueba");
            
            System.out.println("Intentando guardar usuario de prueba...");
            repo.guardar(admin, "test123");
            System.out.println("Usuario guardado exitosamente");
            
            // Verificar que se guardó
            boolean existe = repo.existeCedula("99999999");
            System.out.println("Usuario existe: " + existe);
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
