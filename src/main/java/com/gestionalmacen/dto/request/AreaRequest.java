package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AreaRequest(
    @NotNull @Positive Long sedeId,
    @NotBlank @Size(max = 150) String nombre
) {}
