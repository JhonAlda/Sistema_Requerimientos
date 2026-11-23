package com.gestionalmacen.service.interfaces;
import com.gestionalmacen.dto.request.UnidadMedidaRequest;
import com.gestionalmacen.dto.response.UnidadMedidaResponse;
import java.util.List;
public interface UnidadMedidaService {
    List<UnidadMedidaResponse> listar(); UnidadMedidaResponse obtener(Long id); UnidadMedidaResponse crear(UnidadMedidaRequest request); UnidadMedidaResponse actualizar(Long id, UnidadMedidaRequest request); void desactivar(Long id);
}
