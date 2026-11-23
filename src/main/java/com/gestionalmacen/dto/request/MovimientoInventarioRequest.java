package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record MovimientoInventarioRequest(
    @NotNull @Positive Long almacenId,
    @NotNull @Positive Long productoId,
    @NotNull @DecimalMin("0.001") BigDecimal cantidad,
    @Size(max = 100) String referencia,
    @NotBlank @Size(max = 1000) String observacion
) {}
