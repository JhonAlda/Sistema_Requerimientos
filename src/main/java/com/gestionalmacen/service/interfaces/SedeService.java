package com.gestionalmacen.service.interfaces;
import com.gestionalmacen.dto.request.SedeRequest;
import com.gestionalmacen.dto.response.SedeResponse;
import java.util.List;
public interface SedeService {
    List<SedeResponse> listar(); SedeResponse obtener(Long id); SedeResponse crear(SedeRequest request); SedeResponse actualizar(Long id, SedeRequest request); void desactivar(Long id);
}
