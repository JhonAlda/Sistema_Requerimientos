package com.gestionalmacen.dto.response;

public record LoginResponse(
    Long id,
    String nombres,
    String username,
    String rol
) {}
