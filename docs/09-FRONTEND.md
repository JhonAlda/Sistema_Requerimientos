# Frontend

## Archivos

- `src/main/resources/static/index.html`
- `src/main/resources/static/css/styles.css`
- `src/main/resources/static/js/app.js`
- `src/main/resources/static/js/notification-service.js`

## Tecnologías

- HTML5
- CSS3
- JavaScript
- Fetch API
- Chart.js
- Lucide Icons
- SweetAlert2

## Vistas

- Login
- Dashboard
- Requerimientos
- Despachos
- Inventario
- Las vistas CRUD de usuarios, productos y catálogos no están integradas todavía.

## Integración

Las funciones JavaScript usan `fetch` con credenciales de sesión para consumir `/api/**`. Para POST/PUT/PATCH/DELETE el helper obtiene `GET /api/auth/csrf` y adjunta `X-XSRF-TOKEN`.

`NotificationService` centraliza success, error, warning, info, confirmación y carga. Usa SweetAlert2 cuando el CDN está disponible y un fallback local para mensajes.

## Dashboard

Incluye:

- Requerimientos pendientes.
- Requerimientos parciales.
- Despachos iniciados.
- Productos con stock bajo.
- Consumo por área.
- Productos solicitados.

## Responsividad

Hay adaptaciones para escritorio, tablet y móvil mediante CSS Grid y media queries.

## Límites conocidos

- La empresa aparece como “No configurada”; no hay valor de negocio persistido para el nombre.
- Responsable del encabezado representa el usuario autenticado; no existe un campo independiente de responsable de área.
- La ejecución real de los flujos en navegador todavía no tiene una prueba automatizada end-to-end.
