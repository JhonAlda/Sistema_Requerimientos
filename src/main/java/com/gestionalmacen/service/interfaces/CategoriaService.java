package com.gestionalmacen.service.interfaces;

import com.gestionalmacen.dto.request.CategoriaRequest;
import com.gestionalmacen.dto.response.CategoriaResponse;
import java.util.List;

public interface CategoriaService {
    List<CategoriaResponse> listar();
    CategoriaResponse obtener(Long id);
    CategoriaResponse crear(CategoriaRequest request);
    CategoriaResponse actualizar(Long id, CategoriaRequest request);
    void desactivar(Long id);
}
