package com.gestionalmacen.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record EntregarDespachoRequest(@NotEmpty List<@Valid EntregarProductoRequest> entregas) {}
