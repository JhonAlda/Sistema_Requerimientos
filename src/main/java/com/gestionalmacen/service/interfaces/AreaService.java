package com.gestionalmacen.service.interfaces;
import com.gestionalmacen.dto.request.AreaRequest;
import com.gestionalmacen.dto.response.AreaResponse;
import java.util.List;
public interface AreaService {
    List<AreaResponse> listar(); AreaResponse obtener(Long id); AreaResponse crear(AreaRequest request); AreaResponse actualizar(Long id, AreaRequest request); void desactivar(Long id);
}
