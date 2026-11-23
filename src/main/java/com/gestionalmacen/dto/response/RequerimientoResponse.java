package com.gestionalmacen.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RequerimientoResponse(
    Long id,
    String numero,
    Long areaId,
    String area,
    Long almacenId,
    String almacen,
    Long solicitanteId,
    String solicitante,
    String estado,
    LocalDateTime fechaSolicitud,
    String observaciones,
    List<DetalleRequerimientoResponse> detalles
) {}
