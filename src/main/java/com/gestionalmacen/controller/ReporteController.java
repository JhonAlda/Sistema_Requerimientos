package com.gestionalmacen.controller;

import com.gestionalmacen.dto.response.ReporteCantidadResponse;
import com.gestionalmacen.dto.response.ReporteTiempoResponse;
import com.gestionalmacen.service.interfaces.ReporteService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/consumo-mensual")
    public ResponseEntity<List<ReporteCantidadResponse>> consumoMensual() {
        return ResponseEntity.ok(reporteService.consumoPorArea());
    }

    @GetMapping("/productos-mas-solicitados")
    public ResponseEntity<List<ReporteCantidadResponse>> masSolicitados() {
        return ResponseEntity.ok(reporteService.productosMasSolicitados());
    }

    @GetMapping("/productos-mas-despachados")
    public ResponseEntity<List<ReporteCantidadResponse>> masDespachados() {
        return ResponseEntity.ok(reporteService.productosMasDespachados());
    }

    @GetMapping("/cantidades-pendientes")
    public ResponseEntity<List<ReporteCantidadResponse>> pendientes() {
        return ResponseEntity.ok(reporteService.cantidadesPendientes());
    }

    @GetMapping("/tiempos-atencion")
    public ResponseEntity<ReporteTiempoResponse> tiempos() {
        return ResponseEntity.ok(reporteService.tiemposAtencion());
    }
}
