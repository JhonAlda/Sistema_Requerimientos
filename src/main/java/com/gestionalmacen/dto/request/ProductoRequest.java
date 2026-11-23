package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductoRequest(
    @Size(max = 50) String sku,
    @NotBlank @Size(max = 150) String nombre,
    @NotNull @Positive Long categoriaId,
    @NotNull @Positive Long unidadMedidaId,
    @NotNull @PositiveOrZero @DecimalMin("0.000") BigDecimal stockMinimo
) {}
