# Estado y Pendientes

## Implementado

- Proyecto Maven con Java 21.
- Spring Boot, JPA, Security, Validation y REST.
- Entidades principales.
- Repositorios JPA.
- DTO y controladores REST.
- Login y logout por sesión.
- BCrypt.
- Roles y autorización.
- CRUD de catálogos principales.
- Requerimientos y despachos parciales.
- Inventario transaccional.
- Dashboard y reportes básicos.
- Frontend HTML/CSS/JavaScript.
- SQL Server configurado.
- Configuración de respuestas API uniforme.
- Protección CSRF de sesión y autorización por rol/área.
- Pruebas unitarias de validación, despachos, áreas y último administrador.
- Pruebas MockMvc de token CSRF y respuestas de autenticación.
- Matriz de auditoría funcional actualizada en [[12-matriz-auditoria]].
- Puerto configurado actualmente en `8087`.
- El contexto de Spring arranca y conecta con SQL Server en la suite de pruebas.

## Pendiente o incompleto

- Completar filtros por fecha, área, almacén y producto en reportes.
- Mejorar historial de requerimientos y despachos.
- Agregar exportación PDF y Excel.
- Agregar auditoría real en acciones sensibles.
- Ampliar pruebas de integración/HTTP y automatización de navegador.
- Añadir pantallas frontend para usuarios, productos, categorías y catálogos.
- Añadir formulario UI para entradas y ajustes de inventario.
- Añadir consulta UI e endpoint de historial de movimientos de inventario.
- Agregar cancelación de requerimientos y despachos.
- Agregar índices y consultas optimizadas.
- Separar credenciales reales de `application.properties`.
- Configurar perfiles `dev` y `prod`.
- Definir migraciones controladas antes de producción (Flyway no está habilitado actualmente).
- Restringir CORS al dominio de despliegue y servir con HTTPS.
- Definir y persistir nombre de empresa y responsable real del área.

## Riesgo conocido

`ddl-auto=update` es útil durante desarrollo, pero no reemplaza una estrategia de migración controlada en producción.

## Próxima prioridad

1. Verificar login y flujo de despacho desde navegador.
2. Ejecutar creación de requerimiento con varias líneas y entrega parcial.
3. Confirmar que no haya doble descuento con reintento/concurrencia.
4. Completar pantallas administrativas y ajustes de inventario.
5. Añadir filtros de reportes y exportación.
