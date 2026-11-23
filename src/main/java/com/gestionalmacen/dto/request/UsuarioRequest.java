package com.gestionalmacen.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import com.gestionalmacen.validation.OnCreate;
import com.gestionalmacen.validation.OnUpdate;
import java.util.List;

public record UsuarioRequest(
    @NotBlank @Size(max = 100) String nombres,
    @NotBlank @Size(max = 100) String username,
    @Email @NotBlank @Size(max = 150) String email,
    @NotBlank(groups = OnCreate.class) @Size(min = 8, max = 72, groups = OnCreate.class)
    @Pattern(regexp = "^$|.{8,72}$", groups = OnUpdate.class) String password,
    @NotNull @Positive Long rolId,
    @Positive Long sedeId,
    List<@NotNull @Positive Long> areaIds
) {}
