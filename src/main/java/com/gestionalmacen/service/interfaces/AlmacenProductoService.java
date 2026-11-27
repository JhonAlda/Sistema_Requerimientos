package com.gestionalmacen.service.interfaces;

import java.util.List;

public interface AlmacenProductoService {
    List<Long> productoIdsPorAlmacen(Long almacenId);
}