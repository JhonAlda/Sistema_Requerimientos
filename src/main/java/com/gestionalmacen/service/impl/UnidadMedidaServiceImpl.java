package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.UnidadMedidaRequest;
import com.gestionalmacen.dto.response.UnidadMedidaResponse;
import com.gestionalmacen.entity.UnidadMedida;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.repository.UnidadMedidaRepository;
import com.gestionalmacen.service.interfaces.UnidadMedidaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnidadMedidaServiceImpl implements UnidadMedidaService {
    private final UnidadMedidaRepository repository;

    public UnidadMedidaServiceImpl(UnidadMedidaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedidaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedidaResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public UnidadMedidaResponse crear(UnidadMedidaRequest r) {
        return toResponse(repository.save(UnidadMedida.builder().codigo(r.codigo().trim().toUpperCase())
                .nombre(r.nombre().trim()).activa(true).build()));
    }

    @Override
    public UnidadMedidaResponse actualizar(Long id, UnidadMedidaRequest r) {
        UnidadMedida u = buscar(id);
        u.setCodigo(r.codigo().trim().toUpperCase());
        u.setNombre(r.nombre().trim());
        return toResponse(repository.save(u));
    }

    @Override
    public void desactivar(Long id) {
        UnidadMedida u = buscar(id);
        u.setActiva(false);
        repository.save(u);
    }

    private UnidadMedida buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada: " + id));
    }

    private UnidadMedidaResponse toResponse(UnidadMedida u) {
        return new UnidadMedidaResponse(u.getId(), u.getCodigo(), u.getNombre(), u.getActiva());
    }
}
