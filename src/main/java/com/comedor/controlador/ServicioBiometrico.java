package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ServicioBiometrico {

    // Compara la foto capturada con la almacenada y retorna el porcentaje de similitud
    public double calcularSimilitud(Usuario usuario, File fotoCapturada) throws IOException {
        if (fotoCapturada == null) return 0.0;

        BufferedImage imgSubida = ImageIO.read(fotoCapturada);
        
        System.out.println("Procesando biometría para usuario: " + usuario.obtCedula());

        BufferedImage imgBaseDatos = cargarFotoPorCedula(usuario.obtCedula());

        if (imgBaseDatos == null) {
            System.out.println("⚠️ No se encontró foto específica para " + usuario.obtCedula() + " en /imagenes/usuarios/. Usando imagen de referencia genérica.");
            imgBaseDatos = cargarFotoReferenciaSimulada();
        } else {
            System.out.println("✅ Foto de referencia encontrada en base de datos para: " + usuario.obtCedula());
        }

        if (imgBaseDatos == null) return 100.0;

        double similitud = compararImagenes(imgSubida, imgBaseDatos);
        System.out.println("📊 Similitud biométrica calculada: " + String.format("%.2f", similitud) + "%");
        return similitud;
    }

    // Simula la consulta a la BD de Secretaría buscando un archivo con el nombre de la cédula
    private BufferedImage cargarFotoPorCedula(String cedula) {
        try {
            String[] extensiones = {".jpg", ".png", ".jpeg"};
            
            for (String ext : extensiones) {
                String ruta = "/imagenes/usuarios/" + cedula + ext;
                URL url = getClass().getResource(ruta);
                if (url != null) {
                    return ImageIO.read(url);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo foto de BD para: " + cedula);
        }
        return null;
    }

    // Carga una imagen de referencia por defecto si no se encuentra la del usuario
    private BufferedImage cargarFotoReferenciaSimulada() {
        try {
            URL url = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (url != null) return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Compara dos imágenes píxel a píxel y retorna el porcentaje de similitud
    private double compararImagenes(BufferedImage imgA, BufferedImage imgB) {
        int width = 100;
        int height = 100;
        BufferedImage a = resize(imgA, width, height);
        BufferedImage b = resize(imgB, width, height);

        long diff = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgbA = a.getRGB(x, y);
                int rgbB = b.getRGB(x, y);
                
                int rA = (rgbA >> 16) & 0xFF;
                int gA = (rgbA >> 8) & 0xFF;
                int bA = rgbA & 0xFF;

                int rB = (rgbB >> 16) & 0xFF;
                int gB = (rgbB >> 8) & 0xFF;
                int bB = rgbB & 0xFF;

                int diffR = Math.abs(rA - rB);
                int diffG = Math.abs(gA - gB);
                int diffB = Math.abs(bA - bB);

                diff += (diffR < 30 ? 0 : diffR);
                diff += (diffG < 30 ? 0 : diffG);
                diff += (diffB < 30 ? 0 : diffB);
            }
        }
        long maxDiff = 3L * 255 * width * height;
        return 100.0 - (100.0 * diff / maxDiff);
    }

    // Redimensiona una imagen a las dimensiones especificadas con alta calidad
    private BufferedImage resize(BufferedImage img, int w, int h) {
        BufferedImage dimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dimg.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(img, 0, 0, w, h, null);
        g2d.dispose();
        return dimg;
    }
}