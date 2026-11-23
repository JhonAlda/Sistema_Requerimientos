package com.gestionalmacen.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gestionalmacen.dto.request.UsuarioRequest;
import com.gestionalmacen.entity.Rol;
import com.gestionalmacen.entity.Usuario;
import com.gestionalmacen.enums.NombreRol;
import com.gestionalmacen.exception.BusinessRuleException;
import com.gestionalmacen.repository.AreaRepository;
import com.gestionalmacen.repository.RolRepository;
import com.gestionalmacen.repository.SedeRepository;
import com.gestionalmacen.repository.UsuarioAreaRepository;
import com.gestionalmacen.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private SedeRepository sedeRepository;
    @Mock private AreaRepository areaRepository;
    @Mock private UsuarioAreaRepository usuarioAreaRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UsuarioServiceImpl service;

    @Test
    void noPermiteQuitarRolAlUltimoAdministradorActivo() {
        Usuario administrador = Usuario.builder()
            .id(1L)
            .nombres("Administradora")
            .username("admin")
            .email("admin@example.test")
            .passwordHash("hash")
            .rol(Rol.builder().id(1L).nombre(NombreRol.ADMINISTRADOR).build())
            .activo(true)
            .build();
        Rol rolSolicitante = Rol.builder().id(2L).nombre(NombreRol.SOLICITANTE).build();
        UsuarioRequest request = new UsuarioRequest(
            "Administradora", "admin", "admin@example.test", "NuevaClave123!", 2L, null, null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(administrador));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rolSolicitante));
        when(usuarioRepository.countActivosPorRol(NombreRol.ADMINISTRADOR)).thenReturn(1L);

        assertThrows(BusinessRuleException.class, () -> service.actualizar(1L, request));
        verify(usuarioRepository, never()).save(administrador);
    }

    @Test
    void noPermiteDesactivarAlUltimoAdministradorActivo() {
        Usuario administrador = Usuario.builder()
            .id(1L)
            .nombres("Administradora")
            .username("admin")
            .email("admin@example.test")
            .passwordHash("hash")
            .rol(Rol.builder().id(1L).nombre(NombreRol.ADMINISTRADOR).build())
            .activo(true)
            .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(administrador));
        when(usuarioRepository.countActivosPorRol(NombreRol.ADMINISTRADOR)).thenReturn(1L);

        assertThrows(BusinessRuleException.class, () -> service.desactivar(1L));
        verify(usuarioRepository, never()).save(administrador);
    }
}
