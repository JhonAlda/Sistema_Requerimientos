package com.gestionalmacen.controller;
import com.gestionalmacen.dto.request.UnidadMedidaRequest;
import com.gestionalmacen.dto.response.UnidadMedidaResponse;
import com.gestionalmacen.service.interfaces.UnidadMedidaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/unidades")
public class UnidadController {
    private final UnidadMedidaService service;
    public UnidadController(UnidadMedidaService service) { this.service=service; }
    @GetMapping public ResponseEntity<List<UnidadMedidaResponse>> listar(){return ResponseEntity.ok(service.listar());}
    @GetMapping("/{id}") public ResponseEntity<UnidadMedidaResponse> obtener(@PathVariable Long id){return ResponseEntity.ok(service.obtener(id));}
    @PostMapping public ResponseEntity<UnidadMedidaResponse> crear(@Valid @RequestBody UnidadMedidaRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(r));}
    @PutMapping("/{id}") public ResponseEntity<UnidadMedidaResponse> actualizar(@PathVariable Long id,@Valid @RequestBody UnidadMedidaRequest r){return ResponseEntity.ok(service.actualizar(id,r));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> desactivar(@PathVariable Long id){service.desactivar(id);return ResponseEntity.noContent().build();}
}
