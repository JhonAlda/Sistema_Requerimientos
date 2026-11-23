package com.gestionalmacen.dto.response;

import java.math.BigDecimal;

public record InventarioResponse(
    Long id,
    Long almacenId,
    String almacen,
    Long productoId,
    String producto,
    String unidad,
    BigDecimal existencia,
    BigDecimal stockMinimo,
    Boolean stockBajo
) {}
