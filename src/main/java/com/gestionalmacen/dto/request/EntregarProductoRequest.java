package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record EntregarProductoRequest(
    @NotNull @Positive Long detalleRequerimientoId,
    @NotNull @DecimalMin(value = "0.001") BigDecimal cantidadEntregada
) {}
