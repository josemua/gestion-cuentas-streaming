# Sistema de Gestión de Cuentas de Streaming
![Java](https://img.shields.io/badge/Java-17-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-21-orange)
![Maven](https://img.shields.io/badge/Maven-Build-success)
## 📋 Descripción

Aplicación de escritorio desarrollada en **JavaFX** para la gestión de cuentas de streaming compartidas. Permite administrar plataformas, cuentas, espacios vendidos a clientes y el control de pagos mensuales.

**Trabajo Final - Técnicas de Programación y Laboratorio**
**Universidad de Antioquia - 2025**

## 👨‍💻 Autor

- **Jose Fernando Muñoz Alvarez**
- Ingeniería de Sistemas - UdeA

## 🎯 Funcionalidades

### Módulos Principales

1. **Gestión de Clientes**
    - Registrar, editar, buscar y eliminar clientes
    - Validación de teléfono y correo electrónico
    - Control de espacios asignados

2. **Gestión de Plataformas**
    - Crear y administrar plataformas (Netflix, Disney+, Amazon Prime, etc.)
    - Control de cuentas asociadas por plataforma
    - No permite eliminar plataformas con cuentas activas

3. **Gestión de Cuentas de Streaming**
    - Registrar cuentas con email, contraseña, plan y número máximo de espacios.
    - **Permite usar el mismo correo en diferentes plataformas**
    - Control de espacios máximos y disponibles por cuenta
    - Validación de formato de email

4. **Gestión de Espacios Compartidos**
    - Asignar espacios de cuentas a clientes
    - Precio mensual configurable por espacio
    - Control automático de disponibilidad
    - Liberación de espacios

5. **Gestión de Pagos**
    - Registro de pagos mensuales por espacio
    - Control de deudas pendientes
    - Generación automática de pagos del mes
    - Historial de pagos

## 🏗️ Arquitectura

El proyecto sigue el patrón **MVC (Modelo-Vista-Controlador)**:

```
src/main/java/com/josemuadev/trabajofinal/
├── model/               # Clases de dominio (entidades)
├── negocio/             # Lógica de negocio (administradores)
├── dao/                 # Capa de persistencia (archivos)
├── controller/          # Controladores JavaFX
├── util/                # Utilidades y validaciones
└── App.java             # Clase principal

src/main/resources/com/josemuadev/trabajofinal/   # Interfaz gráfica construida con JavaFX + FXML

```

## 🔧 Tecnologías Utilizadas

- **Java 17** - Lenguaje de programación
- **JavaFX 21.0.6** - Interfaz gráfica de usuario
- **Maven** - Gestión de dependencias y construcción
- **Java Serialization** - Persistencia de datos en archivos binarios

## ⚙️ Requisitos

- JDK 17 o superior
- Maven 3.6+

## 🚀 Ejecución

### Con Maven

```bash
# Navegar al directorio del proyecto
cd trabajo-final

# Compilar y ejecutar con Maven Wrapper (Recomendado)
./mvnw clean javafx:run

# O con Maven instalado globalmente
mvn clean javafx:run
```

### Compilar JAR ejecutable

```bash
# Compilar el proyecto y copiar dependencias
./mvnw clean package

# Ejecutar el JAR generado (requiere las dependencias en target/libs)
java -cp "target/trabajo-final-1.0.0.jar;target/libs/*" com.josemuadev.trabajofinal.App

# En Linux/Mac usar : en lugar de ;
java -cp "target/trabajo-final-1.0.0.jar:target/libs/*" com.josemuadev.trabajofinal.App
```

### Generar Ejecutable para Windows (.exe)

```bash
# 1. Compilar el proyecto
./mvnw clean package

# El proyecto incluye un `pom.xml` configurado para generar un instalador mediante **jpackage**.
cp target/trabajo-final-1.0.0.jar target/libs/

# 3. Generar ejecutable con jpackage (incluido en JDK 14+)
jpackage --input target/libs \
  --name "StreamingManager" \
  --main-jar trabajo-final-1.0.0.jar \
  --main-class com.josemuadev.trabajofinal.App \
  --type app-image \
  --dest target/dist \
  --app-version 1.0.0 \
  --vendor JoseMuADev \
  --module-path target/libs \
  --add-modules javafx.controls,javafx.fxml

# El ejecutable estará en: target/dist/StreamingManager/StreamingManager.exe
```

## 📁 Persistencia de Datos

El sistema guarda automáticamente la información en:
```
{usuario}/.streaming-app/data/
├── clientes.dat          (Información de todos los clientes)
├── plataformas.dat       (Plataformas de streaming)
├── cuentas.dat           (Cuentas con credenciales y espacios)
└── espacios.dat          (Espacios compartidos y asignaciones)
```

El sistema crea automáticamente el directorio si no existe.

## 📜 Reglas de Negocio

### Plataformas
- No puede eliminarse una plataforma si tiene cuentas registradas
- El nombre de la plataforma es obligatorio

### Cuentas
- **Un mismo email puede usarse en múltiples plataformas** (ejemplo: `usuario@mail.com` puede estar en Netflix y Disney+)
- **Un email NO puede duplicarse en la MISMA plataforma**
- El email debe tener formato válido (validación con regex)
- Una cuenta no puede eliminarse si tiene espacios ocupados
- El número máximo de espacios debe ser positivo
- Requiere contraseña y plan

### Espacios
- El número de espacios ocupados no puede superar el máximo permitido por la cuenta
- Cada espacio debe estar asociado a un cliente
- El precio mensual debe ser positivo
- Al eliminar un espacio, se libera automáticamente en la cuenta

### Clientes
- El teléfono y email deben tener formato válido
- Un cliente puede tener múltiples espacios asignados
- No puede eliminarse un cliente si tiene espacios activos

### Pagos
- Cada pago está asociado a un espacio compartido
- El monto del pago debe ser positivo
- Se registra automáticamente la fecha de pago


## 📝 Licencia

Proyecto académico para la Universidad de Antioquia.

---

**Técnicas de Programación y Laboratorio - UdeA 2025**