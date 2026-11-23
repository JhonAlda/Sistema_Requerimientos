# Modelo de Datos

## Entidades principales

- `Sede`
- `Almacen`
- `Area`
- `Rol`
- `Usuario`
- `UsuarioArea`
- `Categoria`
- `UnidadMedida`
- `Producto`
- `EstadoRequerimiento`
- `Requerimiento`
- `DetalleRequerimiento`
- `Despacho`
- `DetalleDespacho`
- `Inventario`
- `MovimientoInventario`
- `Auditoria`

## Relaciones

- Una sede tiene almacenes y áreas.
- Un almacén pertenece a una sede.
- Un área pertenece a una sede.
- Un usuario pertenece a un rol y puede pertenecer a una sede.
- `UsuarioArea` vincula usuarios y áreas.
- Un producto pertenece a categoría y unidad de medida.
- Un requerimiento pertenece a área, almacén y solicitante.
- Un requerimiento contiene varios detalles de producto.
- Un despacho pertenece a requerimiento y usuario despachador.
- Un despacho contiene varias entregas detalladas.
- El inventario es único por almacén y producto.
- Los movimientos pertenecen a inventarios.

## Tipos importantes

- Cantidades: `BigDecimal`.
- Fechas y horas: `LocalDateTime`.
- Estados: enumeraciones Java.
- Identificadores: `Long` con generación automática.
- Desactivación: campos booleanos donde corresponde.

## Estados calculados

- `PENDIENTE`: sin despacho realizado.
- `EN_PROCESO`: despacho iniciado.
- `PARCIAL`: al menos una entrega y cantidades pendientes.
- `ATENDIDO`: todas las cantidades solicitadas fueron entregadas.
