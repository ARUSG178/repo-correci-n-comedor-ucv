package com.comedor.vista.utils;

import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UIConstants {
    // Espaciado basado en múltiplos de 8px
    public static final int SPACING_XS = 4;   // 4px
    public static final int SPACING_SM = 8;   // 8px
    public static final int SPACING_MD = 16;  // 16px
    public static final int SPACING_LG = 24;  // 24px
    public static final int SPACING_XL = 32;  // 32px
    public static final int SPACING_XXL = 48; // 48px

    // Padding uniforme para tarjetas
    public static final EmptyBorder CARD_PADDING = new EmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD);
    public static final EmptyBorder CARD_PADDING_COMPACT = new EmptyBorder(SPACING_SM, SPACING_SM, SPACING_SM, SPACING_SM);
    public static final EmptyBorder CARD_PADDING_LARGE = new EmptyBorder(SPACING_LG, SPACING_LG, SPACING_LG, SPACING_LG);

    // Ancho máximo de contenido
    public static final int MAX_CONTENT_WIDTH = 1200;
    public static final int CARD_MAX_WIDTH = 400;
    public static final int CONTENT_WIDTH_COMPACT = 360;
    public static final int CONTENT_WIDTH_NORMAL = 390;

    // Sombras y efectos visuales
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 60);
    public static final Color SHADOW_LIGHT = new Color(0, 0, 0, 30);
    
    // Gradientes para barras
    public static final Color COLOR_AZUL_GRADIENTE_START = new Color(0, 51, 102);
    public static final Color COLOR_AZUL_GRADIENTE_END = new Color(0, 81, 132);
    
    // Tipografía pesos
    public static final Font FONT_TITLE_BOLD = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_CARD_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_CARD_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    // Métodos de utilidad para espaciado
    public static EmptyBorder spacing(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

    public static EmptyBorder spacingVertical(int size) {
        return new EmptyBorder(size, 0, size, 0);
    }

    public static EmptyBorder spacingHorizontal(int size) {
        return new EmptyBorder(0, size, 0, size);
    }

    public static EmptyBorder spacing(int size) {
        return new EmptyBorder(size, size, size, size);
    }
}
