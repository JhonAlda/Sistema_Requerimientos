package com.gestionalmacen.dto.response;

import java.util.List;

public record ReporteTiempoResponse(
    double esperaPromedioMinutos,
    double preparacionPromedioMinutos,
    double atencionPromedioMinutos,
    List<String> alertas
) {}
