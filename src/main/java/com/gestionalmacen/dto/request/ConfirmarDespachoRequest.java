package com.gestionalmacen.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record ConfirmarDespachoRequest(
    @NotNull @Positive Long requerimientoId,
    @NotEmpty List<@Valid EntregarProductoRequest> entregas
) {}
