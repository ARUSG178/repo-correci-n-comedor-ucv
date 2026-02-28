package com.comedor.controlador;

import com.comedor.modelo.entidades.RegistroCosto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de caja negra para la clase Monedero
 * Técnicas aplicadas:
 * - Partición de equivalencia
 * - Análisis de valores límite
 */

public class ServicioCostoTest {

    private ServicioCosto servicioCosto;
    private final String PERIODO_PRUEBA = "2025-03";

    @BeforeEach
    void setUp() {
        servicioCosto = new ServicioCosto();
    }

    @Nested
    @DisplayName("Pruebas para calcularCostoUnitarioBandeja(String periodo)")
    class CalcularCostoUnitarioBandejaTests {

        @Test
        @DisplayName("CP-CCB-01: Cálculo normal con valores válidos")
        void testCalcularCostoUnitarioNormal() {
            // Arrange
            servicioCosto.registrarProduccionBandejas(PERIODO_PRUEBA, 100);
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.FIJO, 
                                      "Alquiler", 5000.0);
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.VARIABLE, 
                                      "Insumos", 3000.0);

            // Act
            double resultado = servicioCosto.calcularCostoUnitarioBandeja(PERIODO_PRUEBA);

            // Assert
            double totalCostosEsperado = 5000.0 + 3000.0; // 8000
            double costoUnitarioEsperado = totalCostosEsperado / 100; // 80.0
            assertEquals(costoUnitarioEsperado, resultado, 0.001, 
                        "El costo unitario debe ser totalCostos / cantidad");
        }

        @Test
        @DisplayName("CP-CCB-02: Periodo sin producción registrada (cantidad <= 0)")
        void testCalcularCostoUnitarioSinProduccion() {
            // Arrange
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.FIJO, 
                                      "Alquiler", 5000.0);
            // No se registra producción para el periodo

            // Act
            double resultado = servicioCosto.calcularCostoUnitarioBandeja(PERIODO_PRUEBA);

            // Assert
            assertEquals(0.0, resultado, "Debe retornar 0.0 si no hay producción registrada");
        }

        @Test
        @DisplayName("CP-CCB-03: Periodo con producción = 0 explícitamente")
        void testCalcularCostoUnitarioProduccionCero() {
            // Arrange
            servicioCosto.registrarProduccionBandejas(PERIODO_PRUEBA, 0);
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.FIJO, 
                                      "Alquiler", 5000.0);

            // Act
            double resultado = servicioCosto.calcularCostoUnitarioBandeja(PERIODO_PRUEBA);

            // Assert
            assertEquals(0.0, resultado, "Debe retornar 0.0 si la producción es 0");
        }

        @Test
        @DisplayName("CP-CCB-04: Periodo sin costos registrados (totalCostos = 0)")
        void testCalcularCostoUnitarioSinCostos() {
            // Arrange
            servicioCosto.registrarProduccionBandejas(PERIODO_PRUEBA, 100);
            // No se agregan costos

            // Act
            double resultado = servicioCosto.calcularCostoUnitarioBandeja(PERIODO_PRUEBA);

            // Assert
            assertEquals(0.0, resultado, "Debe retornar 0.0 si no hay costos registrados");
        }

        @ParameterizedTest
        @CsvSource({
            "50, 1000, 20.0",   // producción, costo total, costo unitario esperado
            "200, 5000, 25.0",
            "75, 2250, 30.0"
        })
        @DisplayName("CP-CCB-05: Múltiples combinaciones producción/costos")
        void testCalcularCostoUnitarioMultiplesValores(int produccion, double costoTotal, 
                                                       double esperado) {
            // Arrange
            servicioCosto.registrarProduccionBandejas(PERIODO_PRUEBA, produccion);
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.FIJO, 
                                      "Costos varios", costoTotal);

            // Act
            double resultado = servicioCosto.calcularCostoUnitarioBandeja(PERIODO_PRUEBA);

            // Assert
            assertEquals(esperado, resultado, 0.001, 
                        "Costo unitario debe ser " + esperado);
        }
    }

    @Nested
    @DisplayName("Pruebas para calcularCCB(String periodo)")
    class CalcularCCBTests {

        @Test
        @DisplayName("CP-CCB-06: CCB normal con merma incluida")
        void testCalcularCCBNormal() {
            // Arrange
            servicioCosto.registrarProduccionBandejas(PERIODO_PRUEBA, 100);
            servicioCosto.registrarMerma(PERIODO_PRUEBA, 0.10); // 10% de merma
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.FIJO, 
                                      "Alquiler", 5000.0);
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.VARIABLE, 
                                      "Insumos", 3000.0);

            // Act
            double resultado = servicioCosto.calcularCCB(PERIODO_PRUEBA);

            // Assert
            double costoUnitarioBase = (5000.0 + 3000.0) / 100; // 80.0
            double ccbEsperado = costoUnitarioBase * (1 + 0.10); // 88.0
            assertEquals(ccbEsperado, resultado, 0.001, 
                        "CCB debe ser costoUnitario * (1 + merma)");
            
            // Verificar que se actualizó el atributo ccbActual
            assertEquals(ccbEsperado, servicioCosto.obtenerCCBActual(), 0.001,
                        "Debe actualizar ccbActual");
        }

        @Test
        @DisplayName("CP-CCB-07: CCB con merma = 0%")
        void testCalcularCCBSinMerma() {
            // Arrange
            servicioCosto.registrarProduccionBandejas(PERIODO_PRUEBA, 100);
            servicioCosto.registrarMerma(PERIODO_PRUEBA, 0.0); // Sin merma
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.FIJO, 
                                      "Alquiler", 5000.0);

            // Act
            double resultado = servicioCosto.calcularCCB(PERIODO_PRUEBA);

            // Assert
            double costoUnitarioBase = 5000.0 / 100; // 50.0
            double ccbEsperado = costoUnitarioBase; // * (1 + 0) = mismo valor
            assertEquals(ccbEsperado, resultado, 0.001, 
                        "Con merma 0, CCB debe ser igual al costo unitario");
        }

        @Test
        @DisplayName("CP-CCB-08: CCB con producción = 0 (debe retornar 0)")
        void testCalcularCCBProduccionCero() {
            // Arrange
            servicioCosto.registrarProduccionBandejas(PERIODO_PRUEBA, 0);
            servicioCosto.registrarMerma(PERIODO_PRUEBA, 0.10);
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.FIJO, 
                                      "Alquiler", 5000.0);

            // Act
            double resultado = servicioCosto.calcularCCB(PERIODO_PRUEBA);

            // Assert
            assertEquals(0.0, resultado, 
                        "Debe retornar 0.0 si la producción es <= 0");
            
            // Verificar que NO se actualizó ccbActual con valor inválido
            assertEquals(0.0, servicioCosto.obtenerCCBActual(), 
                        "ccbActual debe permanecer en 0 si cálculo falla");
        }

        @Test
        @DisplayName("CP-CCB-09: CCB sin merma registrada (usa valor por defecto)")
        void testCalcularCCBSinMermaRegistrada() {
            // Arrange
            servicioCosto.registrarProduccionBandejas(PERIODO_PRUEBA, 100);
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.FIJO, 
                                      "Alquiler", 5000.0);
            // No se registra merma para el periodo

            // Act
            double resultado = servicioCosto.calcularCCB(PERIODO_PRUEBA);

            // Assert
            double costoUnitarioBase = 5000.0 / 100; // 50.0
            // Debe usar 0.0 como merma por defecto
            assertEquals(costoUnitarioBase, resultado, 0.001, 
                        "Sin merma registrada, debe usar 0.0 como default");
        }

        @ParameterizedTest
        @CsvSource({
            "100, 8000, 0.05, 84.0",    // producción, costos, merma, CCB esperado
            "200, 10000, 0.10, 55.0",
            "50, 3000, 0.20, 72.0",
            "150, 12000, 0.15, 92.0"
        })
        @DisplayName("CP-CCB-10: Múltiples combinaciones para CCB")
        void testCalcularCCBMultiplesValores(int produccion, double costos, 
                                             double merma, double esperado) {
            // Arrange
            servicioCosto.registrarProduccionBandejas(PERIODO_PRUEBA, produccion);
            servicioCosto.registrarMerma(PERIODO_PRUEBA, merma);
            servicioCosto.agregarCosto(PERIODO_PRUEBA, RegistroCosto.TipoCosto.FIJO, 
                                      "Costos", costos);

            // Act
            double resultado = servicioCosto.calcularCCB(PERIODO_PRUEBA);

            // Assert
            assertEquals(esperado, resultado, 0.001, 
                        "CCB calculado incorrecto para los valores dados");
        }
    }

    @Nested
    @DisplayName("Pruebas de integración para calcularRegistrarCCBCompleto")
    class CalcularRegistrarCCBCompletoTests {

        @Test
        @DisplayName("CP-CCB-11: Registro y cálculo completo en un solo paso")
        void testCalcularRegistrarCCBCompleto() {
            // Arrange
            double fijos = 5000.0;
            double variables = 3000.0;
            int produccion = 100;
            double merma = 0.10;

            // Act
            double resultado = servicioCosto.calcularRegistrarCCBCompleto(
                fijos, variables, produccion, merma
            );

            // Assert
            double totalCostos = fijos + variables; // 8000
            double costoUnitarioBase = totalCostos / produccion; // 80.0
            double ccbEsperado = costoUnitarioBase * (1 + merma); // 88.0
            
            assertEquals(ccbEsperado, resultado, 0.001, 
                        "CCB calculado incorrectamente");
            
            // Verificar que se registró la producción
            assertEquals(produccion, 
                servicioCosto.obtenerCCBActualRelacionadoConPeriodo(), 
                "No se registró la producción correctamente");
        }
    }
}