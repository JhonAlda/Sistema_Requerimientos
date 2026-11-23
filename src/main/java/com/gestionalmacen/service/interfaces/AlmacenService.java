package com.gestionalmacen.service.interfaces;
import com.gestionalmacen.dto.request.AlmacenRequest;
import com.gestionalmacen.dto.response.AlmacenResponse;
import java.util.List;
public interface AlmacenService {
    List<AlmacenResponse> listar(); AlmacenResponse obtener(Long id); AlmacenResponse crear(AlmacenRequest request); AlmacenResponse actualizar(Long id, AlmacenRequest request); void desactivar(Long id);
}
