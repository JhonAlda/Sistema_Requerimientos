# Matriz de Auditoría Funcional

Fecha de revisión: 2026-11-19.

## Criterio de estado

- **Prueba automatizada:** ejecutado por prueba JUnit/Mockito.
- **Compila / sintaxis:** código compilado o `node --check`; no implica prueba de navegador.
- **Implementado, falta prueba funcional:** hay manejador y endpoint; no se validó el flujo HTTP completo en esta auditoría.
- **Pendiente:** falta interfaz o funcionalidad real.

## Frontend existente

| Módulo | Botón / control | Acción esperada | Estado comprobado |
|---|---|---|---|
| Login | Ingresar | Autenticar sesión y mostrar nombre/rol | Implementado; falta prueba funcional en navegador |
| Sesión | Cerrar sesión | Invalidar sesión y volver al login | Implementado; falta prueba funcional |
| Seguridad | Token CSRF | Proteger escrituras con cookie y cabecera | Prueba MockMvc: token emitido, POST sin token devuelve 403 |
| Global | Notificaciones | Success/error/warning/info, confirmación y carga | Servicio común instalado; sintaxis JS validada |
| Navegación | Dashboard | Cargar indicadores y gráficos | Implementado; falta prueba funcional |
| Navegación | Requerimientos | Cargar listado y catálogos permitidos | Implementado; solicitantes limitados a sus áreas |
| Navegación | Despachos | Cargar pendientes, parciales y en proceso | Implementado; falta prueba funcional |
| Navegación | Inventario | Cargar existencias | Implementado; falta prueba funcional |
| Requerimientos | Nuevo requerimiento | Cargar catálogos y abrir hoja | Implementado; falta prueba funcional |
| Requerimientos | Buscar producto | Filtrar el catálogo descargado | Implementado; sintaxis JS validada |
| Requerimientos | Categoría | Filtrar productos por categoría | Implementado; sintaxis JS validada |
| Requerimientos | Unidad | Mostrar unidad del producto | Implementado; sintaxis JS validada |
| Requerimientos | Agregar producto | Agregar línea con cantidad decimal y evitar duplicado | Implementado; sintaxis JS validada |
| Requerimientos | Quitar producto | Pedir confirmación y remover la línea | Implementado; falta prueba de navegador |
| Requerimientos | Guardar | Un único POST con todos los detalles | Prueba automatizada: varias líneas decimales, sin validar stock al solicitar |
| Requerimientos | Cancelar hoja | Confirmar descarte y cerrar formulario | Implementado; falta prueba funcional |
| Requerimientos | Ver detalle | Abrir el requerimiento seleccionado en despacho | Implementado; falta prueba de navegador |
| Despachos | Actualizar | Recargar lista | Implementado con estado de carga; falta prueba funcional |
| Despachos | Preparar entrega | Cargar requerimiento e inventario sin crear aún un despacho | Implementado; falta prueba funcional |
| Despachos | Cantidad actual | Limitar por pendiente y stock visible | Implementado en HTML/JS y con bloqueo pesimista en backend |
| Despachos | Confirmar entrega | Crear despacho, descontar stock, guardar movimiento y finalizar en una transacción | Endpoint atómico; prueba automatizada cubre rechazo por stock insuficiente, falta éxito end-to-end |
| Despachos | Volver a pendientes | Cerrar detalle sin crear ni cancelar registros | Implementado; no crea operaciones de backend |
| Inventario | Tabla de existencias | Consultar saldos y alertas | Implementado; falta prueba funcional |
| Inventario | Entrada / ajuste | Registrar movimiento y actualizar saldo desde UI | **Pendiente: existen endpoints, no botones/formulario** |

## Backend y reglas

| Regla / endpoint | Resultado de auditoría |
|---|---|
| DTO de requerimiento | Requiere área, almacén y detalles; IDs positivos; cantidades decimales positivas |
| Registro de requerimiento | Transaccional, usuario/fecha/estado asignados en backend; no bloquea la solicitud por stock |
| Producto inactivo | Rechazado al crear requerimiento y al despachar |
| Duplicados en requerimiento | Rechazados por servicio y restricción de base |
| Despacho parcial | Guarda cantidad entregada por detalle; permite un nuevo despacho para el saldo pendiente |
| Stock insuficiente | Rechazo 409 con producto, cantidad de esta entrega y saldo disponible; prueba automatizada confirma que no guarda movimiento ni saldo |
| Concurrencia de despacho | Bloqueo pesimista de inventario y de detalle para reducir sobreentregas concurrentes |
| Ajuste de inventario | Requiere cantidad, tipo y observación; no permite saldo negativo |
| Último administrador | No puede desactivarse ni perder el rol si es el último activo; pruebas automatizadas |
| Área del solicitante | Listado, consulta y creación restringidos a las áreas asignadas |
| Errores HTTP | Envelope `success/message/data/details/validationErrors`; 400 validación, 401 no autenticado, 403 permisos, 404 no encontrado, 409 conflicto, 500 mensaje genérico |
| Protección CSRF | Token cookie `XSRF-TOKEN` requerido para operaciones de escritura |
| Envío sin CSRF | Prueba MockMvc confirma 403 |
| Credenciales inválidas con CSRF válido | Prueba MockMvc confirma 401 sin filtrar contraseña |
| Selección de área para solicitantes | Prueba/implementación restringe lectura y creación por área asignada |

## Módulos sin interfaz

| Módulo | Situación |
|---|---|
| Usuarios y roles | CRUD REST implementado; interfaz frontend pendiente |
| Productos y categorías | CRUD REST implementado; pantalla de administración pendiente |
| Sedes, almacenes y áreas | CRUD REST implementado; pantalla de administración pendiente |
| Unidades de medida | CRUD REST implementado; pantalla de administración pendiente |
| Reportes | Endpoints básicos; filtros avanzados y exportación PDF/Excel pendientes |
| Auditoría | Entidad y repositorio; registro automático de operaciones todavía pendiente |
| Edición/cancelación de requerimiento | Endpoints y reglas todavía pendientes |
| Anulación/corrección de despacho finalizado | No implementado; no se permite edición directa |
| Movimiento inventario | Consulta de historial y pantalla para entrada/ajustes pendientes |

## Verificación ejecutada

- `mvnw -Dtest=UsuarioServiceImplTest,RequestValidationTest,DespachoServiceImplTest,RequerimientoServiceImplTest test`: pasó.
- `mvnw -Dtest=CsrfIntegrationTest test`: pasó contra el contexto Spring y SQL Server.
- `mvnw -DskipTests compile`: pasó después de aplicar los cambios críticos.
- `node --check src/main/resources/static/js/app.js` y `notification-service.js`: pasó.
- Suite completa `mvnw test`: pasó; los contextos Spring conectaron con SQL Server.
- No se ejecutó el recorrido completo desde navegador con sesión persistente; los controles frontend se verificaron por inspección y sintaxis, no por browser automation.
