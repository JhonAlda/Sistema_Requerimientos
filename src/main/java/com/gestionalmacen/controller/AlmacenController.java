package com.gestionalmacen.controller;
import com.gestionalmacen.dto.request.AlmacenRequest;
import com.gestionalmacen.dto.response.AlmacenResponse;
import com.gestionalmacen.service.interfaces.AlmacenService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/almacenes")
public class AlmacenController {
    private final AlmacenService service;
    public AlmacenController(AlmacenService service) { this.service=service; }
    @GetMapping public ResponseEntity<List<AlmacenResponse>> listar(){return ResponseEntity.ok(service.listar());}
    @GetMapping("/{id}") public ResponseEntity<AlmacenResponse> obtener(@PathVariable Long id){return ResponseEntity.ok(service.obtener(id));}
    @PostMapping public ResponseEntity<AlmacenResponse> crear(@Valid @RequestBody AlmacenRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(r));}
    @PutMapping("/{id}") public ResponseEntity<AlmacenResponse> actualizar(@PathVariable Long id,@Valid @RequestBody AlmacenRequest r){return ResponseEntity.ok(service.actualizar(id,r));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> desactivar(@PathVariable Long id){service.desactivar(id);return ResponseEntity.noContent().build();}
}
