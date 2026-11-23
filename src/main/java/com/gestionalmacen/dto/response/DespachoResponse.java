package com.gestionalmacen.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record DespachoResponse(
    Long id,
    Long requerimientoId,
    String requerimientoNumero,
    Long despachadorId,
    String despachador,
    String estado,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    List<DetalleDespachoResponse> detalles
) {}
