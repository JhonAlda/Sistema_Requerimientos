package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record DetalleRequerimientoRequest(
    @NotNull @Positive Long productoId,
    @NotNull @DecimalMin(value = "0.001") BigDecimal cantidad
) {}
