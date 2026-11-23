package com.gestionalmacen.controller;
import com.gestionalmacen.dto.request.SedeRequest;
import com.gestionalmacen.dto.response.SedeResponse;
import com.gestionalmacen.service.interfaces.SedeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/sedes")
public class SedeController {
    private final SedeService service;
    public SedeController(SedeService service) { this.service=service; }
    @GetMapping public ResponseEntity<List<SedeResponse>> listar(){return ResponseEntity.ok(service.listar());}
    @GetMapping("/{id}") public ResponseEntity<SedeResponse> obtener(@PathVariable Long id){return ResponseEntity.ok(service.obtener(id));}
    @PostMapping public ResponseEntity<SedeResponse> crear(@Valid @RequestBody SedeRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(r));}
    @PutMapping("/{id}") public ResponseEntity<SedeResponse> actualizar(@PathVariable Long id,@Valid @RequestBody SedeRequest r){return ResponseEntity.ok(service.actualizar(id,r));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> desactivar(@PathVariable Long id){service.desactivar(id);return ResponseEntity.noContent().build();}
}
