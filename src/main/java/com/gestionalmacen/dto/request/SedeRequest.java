package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SedeRequest(
    @NotBlank @Size(max = 150) String nombre,
    @Size(max = 255) String direccion
) {}
