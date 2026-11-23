# Arquitectura

## Patrón

Arquitectura por capas:

```text
Frontend
  ↓ Fetch API
Controller
  ↓ DTO
Service / Interfaces
  ↓ Reglas de negocio
Repository / Spring Data JPA
  ↓
Entidad JPA
  ↓
SQL Server
```

## Paquetes

- `controller`: endpoints REST y páginas API.
- `service.interfaces`: contratos de servicios.
- `service.impl`: implementación y reglas de negocio.
- `repository`: acceso a datos.
- `entity`: entidades JPA.
- `dto.request`: datos recibidos.
- `dto.response`: datos devueltos.
- `mapper`: conversión entidad-DTO.
- `security`: autenticación y principal de usuario.
- `config`: seguridad, CORS y OpenAPI.
- `exception`: manejo centralizado de errores.
- `enums`: roles y estados.

## Principios aplicados

- Constructor injection.
- DTO en controladores.
- Sin exposición directa de entidades JPA en respuestas principales.
- Transacciones en servicios de despacho e inventario.
- Cantidades mediante `BigDecimal`.
- Fechas mediante `LocalDateTime`.
- Desactivación lógica para registros históricos.
