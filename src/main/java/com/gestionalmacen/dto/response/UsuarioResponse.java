package com.gestionalmacen.dto.response;

public record UsuarioResponse(
    Long id,
    String nombres,
    String username,
    String email,
    Long rolId,
    String rol,
    Long sedeId,
    Boolean activo
) {}
