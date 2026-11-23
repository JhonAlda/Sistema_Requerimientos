package com.gestionalmacen.service.interfaces;

import com.gestionalmacen.dto.request.UsuarioRequest;
import com.gestionalmacen.dto.response.UsuarioResponse;
import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listar();
    UsuarioResponse obtener(Long id);
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse actualizar(Long id, UsuarioRequest request);
    void desactivar(Long id);
}
