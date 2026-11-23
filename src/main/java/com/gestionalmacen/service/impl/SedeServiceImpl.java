package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.SedeRequest;
import com.gestionalmacen.dto.response.SedeResponse;
import com.gestionalmacen.entity.Sede;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.repository.SedeRepository;
import com.gestionalmacen.service.interfaces.SedeService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SedeServiceImpl implements SedeService {
    private final SedeRepository repository;

    public SedeServiceImpl(SedeRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SedeResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SedeResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public SedeResponse crear(SedeRequest request) {
        return toResponse(repository.save(
                Sede.builder().nombre(request.nombre().trim()).direccion(request.direccion()).activa(true).build()));
    }

    @Override
    public SedeResponse actualizar(Long id, SedeRequest request) {
        Sede sede = buscar(id);
        sede.setNombre(request.nombre().trim());
        sede.setDireccion(request.direccion());
        return toResponse(repository.save(sede));
    }

    @Override
    public void desactivar(Long id) {
        Sede sede = buscar(id);
        sede.setActiva(false);
        repository.save(sede);
    }

    private Sede buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada: " + id));
    }

    private SedeResponse toResponse(Sede s) {
        return new SedeResponse(s.getId(), s.getNombre(), s.getDireccion(), s.getActiva());
    }
}
