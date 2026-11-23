package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.CategoriaRequest;
import com.gestionalmacen.dto.response.CategoriaResponse;
import com.gestionalmacen.entity.Categoria;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.mapper.CategoriaMapper;
import com.gestionalmacen.repository.CategoriaRepository;
import com.gestionalmacen.service.interfaces.CategoriaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(CategoriaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return CategoriaMapper.toResponse(buscar(id));
    }

    @Override
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = Categoria.builder().nombre(request.nombre().trim()).activa(true).build();
        return CategoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscar(id);
        categoria.setNombre(request.nombre().trim());
        return CategoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void desactivar(Long id) {
        Categoria categoria = buscar(id);
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + id));
    }
}
