package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.AlmacenRequest;
import com.gestionalmacen.dto.response.AlmacenResponse;
import com.gestionalmacen.entity.Almacen;
import com.gestionalmacen.entity.Sede;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.repository.AlmacenRepository;
import com.gestionalmacen.repository.SedeRepository;
import com.gestionalmacen.service.interfaces.AlmacenService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlmacenServiceImpl implements AlmacenService {
    private final AlmacenRepository repository;
    private final SedeRepository sedeRepository;

    public AlmacenServiceImpl(AlmacenRepository repository, SedeRepository sedeRepository) {
        this.repository = repository;
        this.sedeRepository = sedeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlmacenResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlmacenResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public AlmacenResponse crear(AlmacenRequest r) {
        Sede sede = buscarSede(r.sedeId());
        return toResponse(repository.save(
                Almacen.builder().sede(sede).codigo(r.codigo().trim()).nombre(r.nombre().trim()).activo(true).build()));
    }

    @Override
    public AlmacenResponse actualizar(Long id, AlmacenRequest r) {
        Almacen a = buscar(id);
        a.setSede(buscarSede(r.sedeId()));
        a.setCodigo(r.codigo().trim());
        a.setNombre(r.nombre().trim());
        return toResponse(repository.save(a));
    }

    @Override
    public void desactivar(Long id) {
        Almacen a = buscar(id);
        a.setActivo(false);
        repository.save(a);
    }

    private Almacen buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado: " + id));
    }

    private Sede buscarSede(Long id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada: " + id));
    }

    private AlmacenResponse toResponse(Almacen a) {
        return new AlmacenResponse(a.getId(), a.getSede().getId(), a.getSede().getNombre(), a.getCodigo(),
                a.getNombre(), a.getActivo());
    }
}
