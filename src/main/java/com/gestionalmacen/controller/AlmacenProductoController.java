package com.gestionalmacen.controller;

import com.gestionalmacen.service.interfaces.AlmacenProductoService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlmacenProductoController {
    private final AlmacenProductoService service;

    public AlmacenProductoController(AlmacenProductoService service) {
        this.service = service;
    }

    @GetMapping("/api/almacenes/{almacenId}/productos")
    public ResponseEntity<List<Long>> productos(@PathVariable Long almacenId) {
        return ResponseEntity.ok(service.productoIdsPorAlmacen(almacenId));
    }
}