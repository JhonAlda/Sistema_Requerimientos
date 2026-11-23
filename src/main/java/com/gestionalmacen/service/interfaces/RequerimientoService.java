package com.gestionalmacen.service.interfaces;

import com.gestionalmacen.dto.request.RequerimientoRequest;
import com.gestionalmacen.dto.response.RequerimientoResponse;
import java.util.List;

public interface RequerimientoService {
    RequerimientoResponse crear(RequerimientoRequest request);
    RequerimientoResponse obtener(Long id);
    List<RequerimientoResponse> listarTodos();
    List<RequerimientoResponse> listarPendientes();
    List<RequerimientoResponse> listarParaDespacho();
    List<RequerimientoResponse> listarPorArea(Long areaId);
}
