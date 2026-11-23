package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AlmacenRequest(
    @NotNull @Positive Long sedeId,
    @NotBlank @Size(max = 30) String codigo,
    @NotBlank @Size(max = 150) String nombre
) {}
