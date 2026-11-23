# API REST

Todas las rutas se encuentran bajo `/api`.

## Autenticación

- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/csrf` (obtiene token para escrituras con sesión)

## Usuarios y roles

- `GET /api/usuarios`
- `POST /api/usuarios`
- `GET /api/usuarios/{id}`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`
- `GET /api/roles`

## Catálogos

- `GET|POST /api/sedes`
- `GET|PUT|DELETE /api/sedes/{id}`
- `GET|POST /api/almacenes`
- `GET|PUT|DELETE /api/almacenes/{id}`
- `GET|POST /api/areas`
- `GET|PUT|DELETE /api/areas/{id}`
- `GET|POST /api/categorias`
- `GET|PUT|DELETE /api/categorias/{id}`
- `GET|POST /api/unidades`
- `GET|PUT|DELETE /api/unidades/{id}`

## Productos

- `GET /api/productos`
- `GET /api/productos/{id}`
- `POST /api/productos`
- `PUT /api/productos/{id}`
- `DELETE /api/productos/{id}`

Soporta filtros `nombre` y `categoriaId`, además de paginación Spring.

## Requerimientos

- `GET /api/requerimientos/{id}`
- `GET /api/requerimientos` (solicitantes reciben solo sus áreas asignadas)
- `GET /api/requerimientos/pendientes`
- `GET /api/requerimientos/para-despacho`
- `GET /api/requerimientos/area/{areaId}`
- `POST /api/requerimientos`

## Despachos

- `POST /api/despachos/confirmar` (inicio, entrega, inventario y finalización en una transacción)
- `POST /api/despachos/iniciar`
- `POST /api/despachos/{id}/entregar`
- `POST /api/despachos/{id}/finalizar`
- `GET /api/despachos/{id}`

## Inventario

- `GET /api/inventario`
- `GET /api/inventario/almacen/{almacenId}`
- `POST /api/inventario/entradas`
- `POST /api/inventario/ajustes`

## Reportes y dashboard

- `GET /api/dashboard`
- `GET /api/reportes/consumo-mensual`
- `GET /api/reportes/tiempos-atencion`
- `GET /api/reportes/productos-mas-solicitados`
- `GET /api/reportes/productos-mas-despachados`
- `GET /api/reportes/cantidades-pendientes`

## Formato de respuesta

Las respuestas API exitosas siguen el envelope:

```json
{
	"success": true,
	"message": "Operación completada correctamente.",
	"data": {},
	"details": [],
	"validationErrors": {},
	"timestamp": "..."
}
```

Los errores usan el mismo esquema con `success: false`. Los errores de validación detallan campos y las reglas de negocio/conflictos devuelven HTTP 409.

## Token CSRF

La aplicación usa sesión HTTP y CSRF activo. Antes de una operación de escritura, obtener token con `GET /api/auth/csrf` y enviar el valor en `X-XSRF-TOKEN`, además de conservar la cookie `XSRF-TOKEN`.
