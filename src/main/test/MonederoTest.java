package com.comedor.modelo.entidades;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas de caja negra para la clase Monedero
 * Técnicas aplicadas:
 * - Partición de equivalencia
 * - Análisis de valores límite
 */

public class MonederoTest {

    private Monedero monedero;
    private Usuario usuarioPropietario;
    private static final double SALDO_INICIAL = 100.0;
    private static final double LIMITE_SALDO = Monedero.LIMITE_SALDO;

    @BeforeEach
    void setUp() {
        // Crear un usuario de prueba con saldo inicial
        usuarioPropietario = new Usuario("12345678", "Juan Pérez", "Estudiante");
        usuarioPropietario.setSaldo(SALDO_INICIAL);
        monedero = new Monedero(usuarioPropietario);
    }

    // ============================================
    // PRUEBAS PARA: recargar(double monto)
    // ============================================
    
    @Nested
    @DisplayName
    class RecargarTests {
        
        @Test
        @DisplayName("CASO VÁLIDO: Recarga con monto positivo dentro del límite")
        void testRecargar_MontoPositivoValido() {
            // Arrange
            double montoRecarga = 50.0;
            double saldoEsperado = SALDO_INICIAL + montoRecarga;
            
            // Act
            monedero.recargar(montoRecarga);
            
            // Assert
            assertEquals(saldoEsperado, monedero.obtSaldo(), 0.001, 
                "El saldo debe incrementarse en el monto de recarga");
            assertThat(monedero.obtSaldo()).isEqualTo(saldoEsperado);
        }
        
        @Test
        @DisplayName("CASO LÍMITE: Recarga con monto cero - NO debe modificar saldo")
        void testRecargar_MontoCero() {
            // Arrange
            double montoRecarga = 0.0;
            double saldoEsperado = SALDO_INICIAL;
            
            // Act
            monedero.recargar(montoRecarga);
            
            // Assert
            assertEquals(saldoEsperado, monedero.obtSaldo(), 
                "El saldo no debe cambiar con monto cero (monto > 0 es condición)");
        }
        
        @Test
        @DisplayName("CASO INVÁLIDO: Recarga con monto negativo - NO debe modificar saldo")
        void testRecargar_MontoNegativo() {
            // Arrange
            double montoRecarga = -25.0;
            double saldoEsperado = SALDO_INICIAL;
            
            // Act
            monedero.recargar(montoRecarga);
            
            // Assert
            assertEquals(saldoEsperado, monedero.obtSaldo(), 
                "El saldo no debe cambiar con monto negativo (monto > 0 es condición)");
        }
        
        @Test
        @DisplayName("CASO LÍMITE SUPERIOR: Recarga justo en el límite permitido")
        void testRecargar_MontoJustoEnLimite() {
            // Arrange
            double montoRecarga = LIMITE_SALDO - SALDO_INICIAL;
            double saldoEsperado = LIMITE_SALDO;
            
            // Act
            monedero.recargar(montoRecarga);
            
            // Assert
            assertEquals(saldoEsperado, monedero.obtSaldo(), 0.001,
                "Debe permitir recarga que alcance exactamente el límite");
        }
        
        @Test
        @DisplayName("CASO INVÁLIDO: Recarga que excede el límite - NO debe modificar saldo")
        void testRecargar_ExcedeLimite() {
            // Arrange
            double montoRecarga = LIMITE_SALDO - SALDO_INICIAL + 1; // Excede por 1
            double saldoEsperado = SALDO_INICIAL;
            
            // Act
            monedero.recargar(montoRecarga);
            
            // Assert
            assertEquals(saldoEsperado, monedero.obtSaldo(), 
                "No debe permitir recarga que exceda el límite de " + LIMITE_SALDO);
        }
        
        @ParameterizedTest
        @CsvSource({
            "0.01, 100.01",     // Mínimo positivo
            "500.0, 600.0",      // Valor típico
            "9899.0, 9999.0"     // Justo en el límite (100 + 9899 = 9999)
        })
        @DisplayName("CASOS VÁLIDOS PARAMETRIZADOS: Recargas exitosas")
        void testRecargar_CasosValidos(double montoRecarga, double saldoEsperado) {
            // Act
            monedero.recargar(montoRecarga);
            
            // Assert
            assertEquals(saldoEsperado, monedero.obtSaldo(), 0.001);
        }
        
        @ParameterizedTest
        @ValueSource(doubles = {-100.0, -1.0, -0.01})
        @DisplayName("CASOS INVÁLIDOS: Recargas con montos negativos")
        void testRecargar_MontosNegativos(double montoRecarga) {
            // Arrange
            double saldoInicial = monedero.obtSaldo();
            
            // Act
            monedero.recargar(montoRecarga);
            
            // Assert
            assertEquals(saldoInicial, monedero.obtSaldo(),
                "Montos negativos no deben modificar el saldo");
        }
    }
    
    // ============================================
    // PRUEBAS PARA: descontar(double monto)
    // ============================================
    
    @Nested
    @DisplayName("Pruebas de descontar() - Técnicas: Partición, valores límite y tabla de decisión")
    class DescontarTests {
        
        @Test
        @DisplayName("CASO VÁLIDO: Descuento con saldo suficiente - debe retornar true")
        void testDescontar_SaldoSuficiente() {
            // Arrange
            double montoDescuento = 30.0;
            double saldoEsperado = SALDO_INICIAL - montoDescuento;
            
            // Act
            boolean resultado = monedero.descontar(montoDescuento);
            
            // Assert
            assertTrue(resultado, "Debe retornar true cuando el descuento es exitoso");
            assertEquals(saldoEsperado, monedero.obtSaldo(), 0.001,
                "El saldo debe disminuir en el monto descontado");
        }
        
        @Test
        @DisplayName("CASO LÍMITE: Descuento exactamente igual al saldo - debe retornar true")
        void testDescontar_ConsumoExacto() {
            // Arrange
            double montoDescuento = SALDO_INICIAL;
            double saldoEsperado = 0.0;
            
            // Act
            boolean resultado = monedero.descontar(montoDescuento);
            
            // Assert
            assertTrue(resultado, "Debe permitir descuento exacto al saldo");
            assertEquals(saldoEsperado, monedero.obtSaldo(), 0.001);
        }
        
        @Test
        @DisplayName("CASO INVÁLIDO: Descuento con saldo insuficiente - debe retornar false")
        void testDescontar_SaldoInsuficiente() {
            // Arrange
            double montoDescuento = SALDO_INICIAL + 50.0; // Mayor al saldo
            double saldoEsperado = SALDO_INICIAL;
            
            // Act
            boolean resultado = monedero.descontar(montoDescuento);
            
            // Assert
            assertFalse(resultado, "Debe retornar false cuando el saldo es insuficiente");
            assertEquals(saldoEsperado, monedero.obtSaldo(), 
                "El saldo no debe modificarse");
        }
        
        @Test
        @DisplayName("CASO LÍMITE: Descuento con monto cero - debe retornar false (monto > 0)")
        void testDescontar_MontoCero() {
            // Arrange
            double montoDescuento = 0.0;
            double saldoEsperado = SALDO_INICIAL;
            
            // Act
            boolean resultado = monedero.descontar(montoDescuento);
            
            // Assert
            assertFalse(resultado, "Monto cero no es válido (monto > 0 es condición)");
            assertEquals(saldoEsperado, monedero.obtSaldo());
        }
        
        @Test
        @DisplayName("CASO INVÁLIDO: Descuento con monto negativo - debe retornar false")
        void testDescontar_MontoNegativo() {
            // Arrange
            double montoDescuento = -20.0;
            double saldoEsperado = SALDO_INICIAL;
            
            // Act
            boolean resultado = monedero.descontar(montoDescuento);
            
            // Assert
            assertFalse(resultado, "Montos negativos no son válidos");
            assertEquals(saldoEsperado, monedero.obtSaldo());
        }
        
        @Test
        @DisplayName("CASO ESQUINA: Muy cerca de saldo insuficiente (monto = saldo + 0.01)")
        void testDescontar_CasiSaldoInsuficiente() {
            // Arrange
            double montoDescuento = SALDO_INICIAL + 0.01;
            double saldoEsperado = SALDO_INICIAL;
            
            // Act
            boolean resultado = monedero.descontar(montoDescuento);
            
            // Assert
            assertFalse(resultado, "No debe permitir descuento que exceda el saldo ni por 0.01");
            assertEquals(saldoEsperado, monedero.obtSaldo());
        }
    }
}