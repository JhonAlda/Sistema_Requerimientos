package com.gestionalmacen.service.interfaces;

import com.gestionalmacen.dto.request.AjusteInventarioRequest;
import com.gestionalmacen.dto.request.MovimientoInventarioRequest;
import com.gestionalmacen.dto.response.InventarioResponse;
import com.gestionalmacen.dto.response.MovimientoInventarioResponse;
import java.util.List;

public interface InventarioService {
    List<InventarioResponse> listar();
    List<InventarioResponse> listarPorAlmacen(Long almacenId);
    MovimientoInventarioResponse entrada(MovimientoInventarioRequest request);
    MovimientoInventarioResponse ajuste(AjusteInventarioRequest request);
}
