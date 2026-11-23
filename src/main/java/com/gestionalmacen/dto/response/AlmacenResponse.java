package com.gestionalmacen.dto.response;

public record AlmacenResponse(Long id, Long sedeId, String sede, String codigo, String nombre, Boolean activo) {}
