package com.gestionalmacen.dto.response;

import java.math.BigDecimal;

public record DetalleRequerimientoResponse(
    Long id,
    Long productoId,
    String producto,
    String categoria,
    String unidad,
    BigDecimal cantidadSolicitada,
    BigDecimal cantidadDespachada,
    BigDecimal cantidadPendiente
) {}
