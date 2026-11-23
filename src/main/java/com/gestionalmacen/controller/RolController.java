package com.gestionalmacen.controller;

import com.gestionalmacen.entity.Rol;
import com.gestionalmacen.repository.RolRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class RolController {
    private final RolRepository rolRepository;

    public RolController(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public ResponseEntity<List<RolResponse>> listar() {
        return ResponseEntity.ok(rolRepository.findAll().stream()
            .map(rol -> new RolResponse(rol.getId(), rol.getNombre().name()))
            .toList());
    }

    public record RolResponse(Long id, String nombre) {}
}
