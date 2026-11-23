package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IniciarDespachoRequest(@NotNull @Positive Long requerimientoId) {}
