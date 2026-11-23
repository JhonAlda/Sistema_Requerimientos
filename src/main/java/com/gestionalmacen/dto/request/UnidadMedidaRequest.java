package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnidadMedidaRequest(
    @NotBlank @Size(max = 20) String codigo,
    @NotBlank @Size(max = 100) String nombre
) {}
