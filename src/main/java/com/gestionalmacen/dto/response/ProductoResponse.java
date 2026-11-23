package com.gestionalmacen.dto.response;

import java.math.BigDecimal;

public record ProductoResponse(
    Long id,
    String sku,
    String nombre,
    Long categoriaId,
    String categoria,
    Long unidadMedidaId,
    String unidadMedida,
    BigDecimal stockMinimo,
    Boolean activo
) {}
