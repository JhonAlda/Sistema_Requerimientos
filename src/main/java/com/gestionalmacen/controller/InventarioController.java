package com.gestionalmacen.controller;

import com.gestionalmacen.dto.request.AjusteInventarioRequest;
import com.gestionalmacen.dto.request.MovimientoInventarioRequest;
import com.gestionalmacen.dto.response.InventarioResponse;
import com.gestionalmacen.dto.response.MovimientoInventarioResponse;
import com.gestionalmacen.service.interfaces.InventarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {
    private final InventarioService service;

    public InventarioController(InventarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/almacen/{almacenId}")
    public ResponseEntity<List<InventarioResponse>> porAlmacen(@PathVariable Long almacenId) {
        return ResponseEntity.ok(service.listarPorAlmacen(almacenId));
    }

    @PostMapping("/entradas")
    @PreAuthorize("hasAnyRole('ALMACENERO','SUPERVISOR','ADMINISTRADOR')")
    public ResponseEntity<MovimientoInventarioResponse> entrada(@Valid @RequestBody MovimientoInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.entrada(request));
    }

    @PostMapping("/ajustes")
    @PreAuthorize("hasAnyRole('ALMACENERO','SUPERVISOR','ADMINISTRADOR')")
    public ResponseEntity<MovimientoInventarioResponse> ajuste(@Valid @RequestBody AjusteInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.ajuste(request));
    }
}
