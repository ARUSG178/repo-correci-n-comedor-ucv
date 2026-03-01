package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ServicioBiometrico {

    // Compara la foto capturada con la almacenada y retorna el porcentaje de similitud
    public double calcularSimilitud(Usuario usuario, File fotoCapturada) throws IOException {
        if (fotoCapturada == null) throw new IOException("No se ha proporcionado una foto para comparar.");

        System.out.println(">>> Iniciando Protocolo de Reconocimiento Facial <<<");
        System.out.println("Usuario: " + usuario.obtCedula());

        // 1. Cargar foto de referencia (BD Secretaría)
        BufferedImage imgReferencia = cargarFotoDeReferencia(usuario.obtCedula());
        
        // 2. Leer foto actual (Captura)
        BufferedImage imgCapturada = ImageIO.read(fotoCapturada);
        if (imgCapturada == null) throw new IOException("El archivo subido no es una imagen válida.");

        // 3. Comparar usando algoritmo de cuadrantes
        double similitud = compararImagenes(imgCapturada, imgReferencia);
        
        System.out.println(">>> Resultado Biometría: " + String.format("%.2f", similitud) + "% de coincidencia.");
        return similitud;
    }

    // Simula la consulta a la BD de Secretaría buscando un archivo con el nombre de la cédula
    private BufferedImage cargarFotoDeReferencia(String cedula) throws IOException {
        // Definimos una carpeta raíz clara para la "Base de Datos" de imágenes
        // Esto soluciona el error de "no encuentra la foto" creando el lugar esperado si no existe.
        File root = new File("imagenes_bd_secretaria");
        
        // Si no existe, la creamos para evitar el error y guiar al usuario
        if (!root.exists()) {
            boolean creada = root.mkdirs();
            if (creada) {
                System.out.println("(!) Carpeta de BD no encontrada. Se ha creado en: " + root.getAbsolutePath());
                System.out.println("(!) Por favor, coloque allí la foto del usuario: " + cedula + ".jpg");
            }
        }

        // Buscamos el archivo con extensiones comunes
        String[] extensiones = {".jpg", ".jpeg", ".png"};
        File archivoEncontrado = null;

        // 1. Búsqueda en carpeta raíz dedicada (Prioridad)
        for (String ext : extensiones) {
            File f = new File(root, cedula + ext);
            if (f.exists()) {
                archivoEncontrado = f;
                break;
            }
        }
        
        // 2. Si no está, búsqueda en rutas de recursos (Legacy/Desarrollo) por si acaso
        if (archivoEncontrado == null) {
            String[] rutasLegacy = {
                "src/main/resources/imagenes/usuarios/",
                "src/main/java/com/comedor/resources/imagenes/usuarios/"
            };
            for (String ruta : rutasLegacy) {
                for (String ext : extensiones) {
                    File f = new File(ruta + cedula + ext);
                    if (f.exists()) {
                        archivoEncontrado = f;
                        break;
                    }
                }
                if (archivoEncontrado != null) break;
            }
        }

        if (archivoEncontrado != null && archivoEncontrado.exists()) {
            System.out.println("✅ Foto de referencia hallada: " + archivoEncontrado.getAbsolutePath());
            BufferedImage img = ImageIO.read(archivoEncontrado);
            if (img == null) throw new IOException("El archivo encontrado está dañado o no es una imagen.");
            return img;
        } else {
            // Mensaje de error claro para el usuario
            throw new IOException("ERROR: No se encuentra la foto de referencia para la cédula " + cedula + 
                ".\n\nSOLUCIÓN: Copie una foto llamada '" + cedula + ".jpg' en la carpeta:\n" + root.getAbsolutePath());
        }
    }

    // Compara dos imágenes píxel a píxel y retorna el porcentaje de similitud
    // MÉTODO MEJORADO: Usa comparación de histogramas por cuadrantes (Grid Histogram).
    // Esto valida no solo los colores, sino dónde están ubicados (ej. cabello arriba, cara centro).
    private double compararImagenes(BufferedImage imgA, BufferedImage imgB) {
        // Normalización de tamaño (150x150)
        int size = 150;
        BufferedImage fixedA = resize(imgA, size, size);
        BufferedImage fixedB = resize(imgB, size, size);

        // Comparación por cuadrantes (2x2)
        int rows = 2;
        int cols = 2;
        int chunkW = size / cols;
        int chunkH = size / rows;

        double similitudAcumulada = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                BufferedImage subA = fixedA.getSubimage(j * chunkW, i * chunkH, chunkW, chunkH);
                BufferedImage subB = fixedB.getSubimage(j * chunkW, i * chunkH, chunkW, chunkH);
                similitudAcumulada += compararHistogramaGlobal(subA, subB);
            }
        }

        return similitudAcumulada / (rows * cols);
    }

    // Compara el histograma global de un fragmento de imagen
    private double compararHistogramaGlobal(BufferedImage imgA, BufferedImage imgB) {
        long[][] histA = calcularHistograma(imgA);
        long[][] histB = calcularHistograma(imgB);

        double simR = interseccionHistograma(histA[0], histB[0]);
        double simG = interseccionHistograma(histA[1], histB[1]);
        double simB = interseccionHistograma(histA[2], histB[2]);

        return (simR + simG + simB) / 3.0 * 100.0;
    }

    private long[][] calcularHistograma(BufferedImage img) {
        long[][] hist = new long[3][256];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                hist[0][(rgb >> 16) & 0xFF]++; // R
                hist[1][(rgb >> 8) & 0xFF]++;  // G
                hist[2][rgb & 0xFF]++;         // B
            }
        }
        return hist;
    }

    private double interseccionHistograma(long[] histA, long[] histB) {
        double interseccion = 0;
        long totalPixeles = 0;

        for (int i = 0; i < 256; i++) {
            interseccion += Math.min(histA[i], histB[i]);
            totalPixeles += histA[i]; // El total es el mismo para ambas imágenes redimensionadas
        }
        return interseccion / totalPixeles;
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