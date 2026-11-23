package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.ProductoRequest;
import com.gestionalmacen.dto.response.ProductoResponse;
import com.gestionalmacen.entity.Categoria;
import com.gestionalmacen.entity.Producto;
import com.gestionalmacen.entity.UnidadMedida;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.mapper.ProductoMapper;
import com.gestionalmacen.repository.CategoriaRepository;
import com.gestionalmacen.repository.ProductoRepository;
import com.gestionalmacen.repository.UnidadMedidaRepository;
import com.gestionalmacen.service.interfaces.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               UnidadMedidaRepository unidadMedidaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listar(String nombre, Long categoriaId, Pageable pageable) {
        String filtro = nombre == null || nombre.isBlank() ? null : nombre.trim();
        return productoRepository.buscar(filtro, categoriaId, pageable).map(ProductoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoMapper.toResponse(buscar(id));
    }

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = construir(request);
        return ProductoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscar(id);
        Categoria categoria = buscarCategoria(request.categoriaId());
        UnidadMedida unidad = buscarUnidad(request.unidadMedidaId());
        producto.setSku(request.sku() == null ? null : request.sku().trim());
        producto.setNombre(request.nombre().trim());
        producto.setCategoria(categoria);
        producto.setUnidadMedida(unidad);
        producto.setStockMinimo(request.stockMinimo());
        return ProductoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public void desactivar(Long id) {
        Producto producto = buscar(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private Producto construir(ProductoRequest request) {
        return Producto.builder()
            .sku(request.sku() == null ? null : request.sku().trim())
            .nombre(request.nombre().trim())
            .categoria(buscarCategoria(request.categoriaId()))
            .unidadMedida(buscarUnidad(request.unidadMedidaId()))
            .stockMinimo(request.stockMinimo())
            .activo(true)
            .build();
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + id));
    }

    private UnidadMedida buscarUnidad(Long id) {
        return unidadMedidaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada: " + id));
    }
}
