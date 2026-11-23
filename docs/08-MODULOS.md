# Módulos de Negocio

## Requerimientos

Permite crear solicitudes con múltiples productos.

Validaciones:

- Área y almacén existentes.
- Lista de detalles obligatoria.
- Producto no repetido dentro del requerimiento.
- Cantidad mayor a cero.
- Fecha y solicitante asignados por el servidor.
- Número correlativo generado por el servidor.

## Despachos

Permite revisar requerimientos pendientes/en proceso/parciales y registrar entregas parciales o completas.

Reglas:

- El requerimiento no puede estar cancelado o atendido.
- Las entregas no pueden superar la cantidad pendiente.
- No se permiten líneas repetidas en un despacho.
- El stock debe existir y ser suficiente.
- El inventario se bloquea durante el descuento.
- El movimiento conserva saldo resultante y referencia.
- `POST /api/despachos/confirmar` inicia, registra entrega, actualiza stock y finaliza en una sola transacción.
- No se crea un despacho hasta que el almacenero confirma cantidades.
- Stock insuficiente devuelve el producto, cantidad de esta entrega, disponible y alternativa parcial.
- Los detalles de requerimiento e inventario usan bloqueo pesimista durante la confirmación.

## Inventario

Operaciones:

- Consulta general.
- Consulta por almacén.
- Entradas.
- Ajustes positivos.
- Ajustes negativos.

Protecciones:

- Existencia mínima cero.
- Bloqueo pesimista.
- Movimiento por cada operación.
- Alerta de stock bajo.
- Los ajustes requieren observación y responsable autenticado.

## Reportes

- Consumo por área.
- Productos más solicitados.
- Productos más despachados.
- Cantidades pendientes.
- Tiempos de atención.
- Indicadores del dashboard.

## Validaciones compartidas

- DTO con Jakarta Bean Validation.
- Verificación de roles y áreas en backend.
- Respuestas en envelope común `success/message/data/details/validationErrors`.
- Notificaciones centralizadas y botones con estado de carga en los flujos principales.

Tiempos calculados:

- Espera: inicio - solicitud.
- Preparación: fin - inicio.
- Atención total: fin - solicitud.
