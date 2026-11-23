package com.gestionalmacen.dto.response;

import java.math.BigDecimal;

public record DetalleDespachoResponse(
    Long id,
    Long detalleRequerimientoId,
    Long productoId,
    String producto,
    BigDecimal cantidadEntregada
) {}
