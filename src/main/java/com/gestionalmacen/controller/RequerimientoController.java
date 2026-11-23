package com.gestionalmacen.controller;

import com.gestionalmacen.dto.request.RequerimientoRequest;
import com.gestionalmacen.dto.response.RequerimientoResponse;
import com.gestionalmacen.service.interfaces.RequerimientoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requerimientos")
public class RequerimientoController {
    private final RequerimientoService service;

    public RequerimientoController(RequerimientoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RequerimientoResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SOLICITANTE','SUPERVISOR','ADMINISTRADOR')")
    public ResponseEntity<RequerimientoResponse> crear(@Valid @RequestBody RequerimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequerimientoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<RequerimientoResponse>> pendientes() {
        return ResponseEntity.ok(service.listarPendientes());
    }

    @GetMapping("/para-despacho")
    @PreAuthorize("hasAnyRole('ALMACENERO','SUPERVISOR','ADMINISTRADOR')")
    public ResponseEntity<List<RequerimientoResponse>> paraDespacho() {
        return ResponseEntity.ok(service.listarParaDespacho());
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<List<RequerimientoResponse>> porArea(@PathVariable Long areaId) {
        return ResponseEntity.ok(service.listarPorArea(areaId));
    }
}
