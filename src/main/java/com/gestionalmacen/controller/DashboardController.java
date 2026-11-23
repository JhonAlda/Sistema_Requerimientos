package com.gestionalmacen.controller;

import com.gestionalmacen.dto.response.DashboardResponse;
import com.gestionalmacen.service.interfaces.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ReporteService reporteService;

    public DashboardController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> resumen() {
        return ResponseEntity.ok(reporteService.dashboard());
    }
}
