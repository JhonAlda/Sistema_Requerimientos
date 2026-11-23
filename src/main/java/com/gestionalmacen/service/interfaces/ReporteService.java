package com.gestionalmacen.service.interfaces;

import com.gestionalmacen.dto.response.DashboardResponse;
import com.gestionalmacen.dto.response.ReporteCantidadResponse;
import com.gestionalmacen.dto.response.ReporteTiempoResponse;
import java.util.List;

public interface ReporteService {
    DashboardResponse dashboard();
    List<ReporteCantidadResponse> consumoPorArea();
    List<ReporteCantidadResponse> productosMasSolicitados();
    List<ReporteCantidadResponse> productosMasDespachados();
    List<ReporteCantidadResponse> cantidadesPendientes();
    ReporteTiempoResponse tiemposAtencion();
}
