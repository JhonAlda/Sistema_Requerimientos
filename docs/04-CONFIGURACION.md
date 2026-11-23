# Configuración

Archivo principal: `src/main/resources/application.properties`.

## SQL Server

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=SistemaR_BD;encrypt=true;trustServerCertificate=true;loginTimeout=30;
spring.datasource.username=<usuario-local>
spring.datasource.password=<clave-local>
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

## JPA

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

`update` permite que Hibernate ajuste el esquema durante desarrollo. Para producción conviene retirar de forma controlada las operaciones automáticas y adoptar migraciones versionadas.

## Servidor

```properties
server.port=8087
```

## Seguridad de credenciales

El entorno local puede guardar sus credenciales en `application.properties`, pero ese archivo no debe publicarse con claves reales. En producción, usar variables de entorno o un gestor de secretos. No copiar los valores locales al repositorio ni a Obsidian.

Ejemplo:

```powershell
$env:DB_USERNAME='usuario'
$env:DB_PASSWORD='contraseña'
```

La configuración actual espera SQL Server 16 en `localhost:1433`, base `SistemaR_BD`, y utiliza Hibernate `ddl-auto=update`. No se emplean Flyway ni MySQL en la configuración vigente.

