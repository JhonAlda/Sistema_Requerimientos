package com.gestionalmacen.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoInventarioResponse(
    Long id,
    String tipo,
    Long almacenId,
    Long productoId,
    BigDecimal cantidad,
    BigDecimal saldoResultante,
    LocalDateTime fecha,
    String referencia,
    String observacion
) {}
