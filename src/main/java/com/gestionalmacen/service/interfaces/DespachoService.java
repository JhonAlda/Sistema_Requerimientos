package com.gestionalmacen.service.interfaces;

import com.gestionalmacen.dto.request.EntregarDespachoRequest;
import com.gestionalmacen.dto.request.ConfirmarDespachoRequest;
import com.gestionalmacen.dto.request.IniciarDespachoRequest;
import com.gestionalmacen.dto.response.DespachoResponse;

public interface DespachoService {
    DespachoResponse confirmar(ConfirmarDespachoRequest request);
    DespachoResponse iniciar(IniciarDespachoRequest request);
    DespachoResponse entregar(Long despachoId, EntregarDespachoRequest request);
    DespachoResponse finalizar(Long despachoId);
    DespachoResponse obtener(Long despachoId);
}
