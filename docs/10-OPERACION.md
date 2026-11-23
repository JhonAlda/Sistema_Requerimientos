# Operación

## Inicio diario

1. Verificar SQL Server activo.
2. Verificar la base `SistemaR_BD`.
3. Verificar usuario `sack` y contraseña.
4. Ejecutar Maven Wrapper.
5. Abrir `http://localhost:8087`.

## Flujo de requerimiento

1. El solicitante crea requerimiento.
2. Selecciona área y almacén.
3. Agrega uno o varios productos.
4. El sistema genera número correlativo y fecha.
5. El requerimiento queda pendiente.

## Flujo de despacho parcial

1. El almacenero inicia el despacho.
2. El sistema registra despachador y hora de inicio.
3. Se registran cantidades efectivamente entregadas.
4. Se descuenta inventario.
5. Se conserva el detalle del despacho.
6. El requerimiento pasa a parcial si quedan pendientes.
7. El requerimiento conserva cantidades pendientes; se puede registrar otro despacho.
8. Al completar todas las líneas pasa a atendido.
9. Al finalizar se registra fecha final.

La interfaz confirma el despacho en una sola solicitud. Si stock o validaciones fallan, la transacción completa se revierte.

## Flujo de inventario

1. Registrar entrada o ajuste.
2. Bloquear inventario correspondiente.
3. Validar resultado.
4. Guardar movimiento.
5. Actualizar existencias.

## Revisión del sistema

- Swagger/OpenAPI.
- Dashboard.
- Inventario por almacén.
- Requerimientos pendientes.
- Historial de movimientos: disponible en persistencia; pantalla de consulta todavía pendiente.
