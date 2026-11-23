package com.gestionalmacen.dto.response;

import java.math.BigDecimal;

public record DashboardResponse(
    long requerimientosPendientes,
    long requerimientosParciales,
    long despachosIniciados,
    long productosStockBajo,
    BigDecimal pendientesPorDespachar
) {}
