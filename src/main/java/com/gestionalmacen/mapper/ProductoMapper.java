package com.gestionalmacen.mapper;

import com.gestionalmacen.dto.response.ProductoResponse;
import com.gestionalmacen.entity.Producto;

public final class ProductoMapper {
    private ProductoMapper() {}

    public static ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
            producto.getId(),
            producto.getSku(),
            producto.getNombre(),
            producto.getCategoria().getId(),
            producto.getCategoria().getNombre(),
            producto.getUnidadMedida().getId(),
            producto.getUnidadMedida().getCodigo(),
            producto.getStockMinimo(),
            producto.getActivo()
        );
    }
}
