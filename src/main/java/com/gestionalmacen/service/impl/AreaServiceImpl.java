package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.AreaRequest;
import com.gestionalmacen.dto.response.AreaResponse;
import com.gestionalmacen.entity.Area;
import com.gestionalmacen.entity.Sede;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.repository.AreaRepository;
import com.gestionalmacen.repository.SedeRepository;
import com.gestionalmacen.service.interfaces.AreaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AreaServiceImpl implements AreaService {
    private final AreaRepository repository;
    private final SedeRepository sedeRepository;

    public AreaServiceImpl(AreaRepository repository, SedeRepository sedeRepository) {
        this.repository = repository;
        this.sedeRepository = sedeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AreaResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public AreaResponse crear(AreaRequest r) {
        Sede sede = buscarSede(r.sedeId());
        return toResponse(repository.save(Area.builder().sede(sede).nombre(r.nombre().trim()).activa(true).build()));
    }

    @Override
    public AreaResponse actualizar(Long id, AreaRequest r) {
        Area area = buscar(id);
        area.setSede(buscarSede(r.sedeId()));
        area.setNombre(r.nombre().trim());
        return toResponse(repository.save(area));
    }

    @Override
    public void desactivar(Long id) {
        Area area = buscar(id);
        area.setActiva(false);
        repository.save(area);
    }

    private Area buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Área no encontrada: " + id));
    }

    private Sede buscarSede(Long id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada: " + id));
    }

    private AreaResponse toResponse(Area a) {
        return new AreaResponse(a.getId(), a.getSede().getId(), a.getSede().getNombre(), a.getNombre(), a.getActiva());
    }
}
