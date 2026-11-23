# Instalación

## Requisitos

- Windows
- Java 21
- Maven Wrapper incluido
- SQL Server local
- Base `SistemaR_BD`
- Instancia accesible en `localhost:1433`
- VS Code con Java Extension Pack
- Node.js solo si se desea validar sintaxis con `node --check`

## Compilar

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd clean compile
```

## Ejecutar pruebas

```powershell
.\mvnw.cmd test
```

## Ejecutar

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd spring-boot:run
```

También puede ejecutarse desde VS Code con `Run and Debug` y la configuración `GestionAlmacenApplication`, o con `Terminal > Run Task > Spring Boot: ejecutar`.

## Acceso

- Aplicación: `http://localhost:8087`
- Swagger: `http://localhost:8087/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8087/v3/api-docs`

## Base de datos

El proyecto actual usa SQL Server. Hibernate utiliza `ddl-auto=update`, por lo que crea o modifica tablas compatibles durante el arranque según las entidades JPA. No está configurado Flyway.
