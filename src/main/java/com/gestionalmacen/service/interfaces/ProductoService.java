package com.gestionalmacen.service.interfaces;

import com.gestionalmacen.dto.request.ProductoRequest;
import com.gestionalmacen.dto.response.ProductoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    Page<ProductoResponse> listar(String nombre, Long categoriaId, Pageable pageable);
    ProductoResponse obtener(Long id);
    ProductoResponse crear(ProductoRequest request);
    ProductoResponse actualizar(Long id, ProductoRequest request);
    void desactivar(Long id);
}
