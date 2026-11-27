package com.gestionalmacen.service.impl;

import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.repository.AlmacenRepository;
import com.gestionalmacen.repository.InventarioRepository;
import com.gestionalmacen.service.interfaces.AlmacenProductoService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlmacenProductoServiceImpl implements AlmacenProductoService {
    private final InventarioRepository inventarioRepository;
    private final AlmacenRepository almacenRepository;

    public AlmacenProductoServiceImpl(InventarioRepository inventarioRepository,
                                      AlmacenRepository almacenRepository) {
        this.inventarioRepository = inventarioRepository;
        this.almacenRepository = almacenRepository;
    }

    @Override
    public List<Long> productoIdsPorAlmacen(Long almacenId) {
        if (!almacenRepository.existsById(almacenId)) {
            throw new ResourceNotFoundException("Almacén no encontrado: " + almacenId);
        }
        return inventarioRepository.findProductoIdsByAlmacenId(almacenId);
    }
}