package com.gestionalmacen.controller;

import com.gestionalmacen.dto.request.AreaRequest;
import com.gestionalmacen.dto.response.AreaResponse;
import com.gestionalmacen.service.interfaces.AreaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/areas")
public class AreaController {
    private final AreaService service;
    public AreaController(AreaService service) { this.service=service; }
    @GetMapping public ResponseEntity<List<AreaResponse>> listar(){return ResponseEntity.ok(service.listar());}
    @GetMapping("/{id}") public ResponseEntity<AreaResponse> obtener(@PathVariable Long id){return ResponseEntity.ok(service.obtener(id));}
    @PostMapping public ResponseEntity<AreaResponse> crear(@Valid @RequestBody AreaRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(r));}
    @PutMapping("/{id}") public ResponseEntity<AreaResponse> actualizar(@PathVariable Long id,@Valid @RequestBody AreaRequest r){return ResponseEntity.ok(service.actualizar(id,r));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> desactivar(@PathVariable Long id){service.desactivar(id);return ResponseEntity.noContent().build();}
}
