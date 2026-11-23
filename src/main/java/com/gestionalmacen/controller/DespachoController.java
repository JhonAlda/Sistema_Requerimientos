package com.gestionalmacen.controller;

import com.gestionalmacen.dto.request.EntregarDespachoRequest;
import com.gestionalmacen.dto.request.ConfirmarDespachoRequest;
import com.gestionalmacen.dto.request.IniciarDespachoRequest;
import com.gestionalmacen.dto.response.DespachoResponse;
import com.gestionalmacen.service.interfaces.DespachoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/despachos")
public class DespachoController {
    private final DespachoService service;

    public DespachoController(DespachoService service) {
        this.service = service;
    }

    @PostMapping("/confirmar")
    @PreAuthorize("hasAnyRole('ALMACENERO','SUPERVISOR','ADMINISTRADOR')")
    public ResponseEntity<DespachoResponse> confirmar(@Valid @RequestBody ConfirmarDespachoRequest request) {
        return ResponseEntity.ok(service.confirmar(request));
    }

    @PostMapping("/iniciar")
    @PreAuthorize("hasAnyRole('ALMACENERO','SUPERVISOR','ADMINISTRADOR')")
    public ResponseEntity<DespachoResponse> iniciar(@Valid @RequestBody IniciarDespachoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.iniciar(request));
    }

    @PostMapping("/{id}/entregar")
    @PreAuthorize("hasAnyRole('ALMACENERO','SUPERVISOR','ADMINISTRADOR')")
    public ResponseEntity<DespachoResponse> entregar(@PathVariable Long id,
                                                     @Valid @RequestBody EntregarDespachoRequest request) {
        return ResponseEntity.ok(service.entregar(id, request));
    }

    @PostMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ALMACENERO','SUPERVISOR','ADMINISTRADOR')")
    public ResponseEntity<DespachoResponse> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(service.finalizar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespachoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }
}
