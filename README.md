# Sistema de Asignación y Gestión del Comedor (SAGC) - UCV

*Modernizando la experiencia del comedor universitario en la Universidad Central de Venezuela.*

## Descripción

**SAGC** es un sistema de escritorio desarrollado en Java, diseñado para optimizar la gestión integral del comedor de la UCV. El software aborda el registro de usuarios (estudiantes, empleados, administradores), el control de acceso, la configuración dinámica de menús, y la gestión de costos y tarifas, centralizando las operaciones en una aplicación robusta y fácil de usar.

## Misión

Proveer una herramienta tecnológica eficiente, moderna y accesible para la administración de los servicios del comedor universitario de la UCV, mejorando significativamente la experiencia diaria de comensales y personal administrativo.

## Visión

Ser la solución tecnológica de referencia para la gestión de comedor universitarios, impulsando la excelencia operativa, la transparencia en la gestión de costos y la satisfacción del usuario a través de la innovación continua.

---

## Características Principales

- **Gestión de Usuarios:** Registro y autenticación diferenciada por roles (Estudiante, Empleado, Administrador).
- **Panel de Administrador:** Control total sobre el menú (desayuno y almuerzo), incluyendo nombres de platillos, precios e imágenes.
- **Cálculo de Costos:** Módulo para registrar costos fijos/variables y calcular el Costo Conforme a Bandeja (CCB) para ajustar tarifas.
- **Tarifas Dinámicas:** Asignación de precios personalizada según el tipo de usuario, aplicando subsidios para estudiantes y empleados.
- **Interfaz de Usuario Intuitiva:** Vista clara del menú diario para que los usuarios seleccionen su platillo y procedan a la reserva.
- **Persistencia de Datos:** La configuración del menú se guarda localmente en un archivo `menu_config.properties` para facilitar la gestión y mantener la consistencia.

## Tecnologías Utilizadas

- **Lenguaje:** Java (JDK 17)
- **Interfaz Gráfica (GUI):** Java Swing (utilizando exclusivamente las librerías estándar del JDK: `javax.swing`, `java.awt`).
- **Manejo de Datos:** `java.io` y `java.nio` para la persistencia de configuraciones en archivos locales (`.properties`).
- **Estructuras de Datos:** `java.util` para la gestión de colecciones y la implementación de la lógica de negocio.

## Instrucciones de Uso

### Requisitos Previos

- Tener instalado el **JDK (Java Development Kit) versión 17** o superior.
- Un IDE de Java como IntelliJ IDEA, Eclipse o VS Code (con sus respectivas extensiones para Java).

### Instalación y Ejecución

1. Clona o descarga este repositorio en tu máquina local.
2. Abre el proyecto en tu IDE de preferencia.
3. Localiza la clase `com.comedor.Main.java` y ejecútelo para mostrar la ventada de inicio de sesión.

### Flujo Básico del Sistema

1. **Registro:** Los nuevos usuarios deben registrarse proporcionando su cédula y otros datos. El sistema valida la información contra un registro simulado de la UCV. Los administradores requieren un código especial de un solo uso para su registro.
2. **Inicio de Sesión:** Los usuarios acceden con su cédula y contraseña.
3. **Vista de Usuario (Estudiante/Empleado):** Tras iniciar sesión, ven el menú de la semana (desayuno y almuerzo) con los precios calculados según su rol. Pueden seleccionar un platillo para continuar con el proceso de reserva de turno.
4. **Vista de Administrador:** El administrador tiene acceso a un panel de control donde puede:
    - Modificar el nombre y el precio del desayuno y del almuerzo.
    - Cambiar la imagen de cada platillo subiendo un archivo local.
    - Guardar los cambios, que se reflejarán inmediatamente para todos los usuarios.

## Creadores

- **Brian Acosta**
- **Jesús Soto**
- **Juan Dos Ramos**
