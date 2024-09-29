# Proyecto 1 POO - Parqueos Callejeros

## Integrantes

- Luis Gerardo Urbina Salazar, carne 2023156802
- Estebitan UwU

## Descripción

Este proyecto es una simulación de un sistema de parqueo de vehículos en una callejero. El sistema permite la gestión de usuarios, vehículos, espacios de parqueo, reservas y multas.

## Estructura de Archivos

- `src/main/java/com/parqueos/builders/`: Contiene la implementación del patrón de diseño Builder para la creación de objetos.
- `src/main/java/com/parqueos/modelo/`: Contiene las clases y estructuras de datos para representar los objetos del sistema.
- `src/main/java/com/parqueos/reportes/`: Contiene los tipos de reportes que se pueden generar.
- `src/main/java/com/parqueos/servicios/`: Contiene servicios como SistemaParqueo, AuthService, etc.
- `src/main/java/com/parqueos/ui/`: Contiene las vistas y controladores de la interfaz de usuario.
- `src/main/java/com/parqueos/util/`: Contiene utilidades como Validaciones, etc.

## Diagrama de clases

Pendiente subir la foto del diagrama de clases.

### Explicación de los patrones de diseño utilizados

- **Builder**: Se utiliza el patrón de diseño Builder para la creación de Usuarios.
- **Factory**: Se utiliza el patrón de diseño Factory para la creación de Reportes.
- **Singleton**: Se utiliza el patrón de diseño Singleton para la Configuraciones de Parqueo.
- **MVC (Modelo-Vista-Controlador)**: Se utiliza el patrón de diseño MVC para la separación de responsabilidades entre la lógica, la presentación y la persistencia.

## Como ejecutar el proyecto

1. Compilar el proyecto:

```bash
javac -d out -sourcepath src/main/java src/main/java/com/parqueos/ui/Main.java
```

2. Ejecutar el proyecto:

```bash
java -cp out com.parqueos.ui.Main
```

