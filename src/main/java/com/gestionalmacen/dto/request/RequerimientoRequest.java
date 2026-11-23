package com.gestionalmacen.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record RequerimientoRequest(
    @NotNull @Positive Long areaId,
    @NotNull @Positive Long almacenId,
    @Size(max = 1000) String observaciones,
    @NotEmpty List<@Valid DetalleRequerimientoRequest> detalles
) {}
