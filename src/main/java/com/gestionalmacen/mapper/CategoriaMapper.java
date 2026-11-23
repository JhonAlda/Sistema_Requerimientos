package com.gestionalmacen.mapper;

import com.gestionalmacen.dto.response.CategoriaResponse;
import com.gestionalmacen.entity.Categoria;

public final class CategoriaMapper {
    private CategoriaMapper() {}

    public static CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getActiva());
    }
}
