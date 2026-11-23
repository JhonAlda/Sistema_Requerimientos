package com.gestionalmacen.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gestionalmacen.dto.request.DetalleRequerimientoRequest;
import com.gestionalmacen.dto.request.RequerimientoRequest;
import com.gestionalmacen.entity.Area;
import com.gestionalmacen.entity.Almacen;
import com.gestionalmacen.entity.Categoria;
import com.gestionalmacen.entity.EstadoRequerimiento;
import com.gestionalmacen.entity.Producto;
import com.gestionalmacen.entity.Rol;
import com.gestionalmacen.entity.UnidadMedida;
import com.gestionalmacen.entity.Usuario;
import com.gestionalmacen.enums.EstadoRequerimientoNombre;
import com.gestionalmacen.enums.NombreRol;
import com.gestionalmacen.repository.AlmacenRepository;
import com.gestionalmacen.repository.AreaRepository;
import com.gestionalmacen.repository.DetalleRequerimientoRepository;
import com.gestionalmacen.repository.EstadoRequerimientoRepository;
import com.gestionalmacen.repository.ProductoRepository;
import com.gestionalmacen.repository.RequerimientoRepository;
import com.gestionalmacen.repository.UsuarioAreaRepository;
import com.gestionalmacen.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class RequerimientoServiceImplTest {
    @Mock private RequerimientoRepository requerimientoRepository;
    @Mock private DetalleRequerimientoRepository detalleRepository;
    @Mock private EstadoRequerimientoRepository estadoRepository;
    @Mock private AreaRepository areaRepository;
    @Mock private AlmacenRepository almacenRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioAreaRepository usuarioAreaRepository;

    @InjectMocks private RequerimientoServiceImpl service;

    @AfterEach
    void limpiarSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registraVariasLineasDecimalesSinBloquearPorStock() {
        var role = Rol.builder().id(1L).nombre(NombreRol.SOLICITANTE).build();
        var user = Usuario.builder().id(4L).nombres("Solicitante").username("solicita")
            .rol(role).activo(true).build();
        var area = Area.builder().id(2L).nombre("Cocina - Turno mañana").activa(true).build();
        var warehouse = Almacen.builder().id(3L).nombre("Principal").codigo("A-1").activo(true).build();
        var state = EstadoRequerimiento.builder().id(1L).nombre(EstadoRequerimientoNombre.PENDIENTE).build();
        var category = Categoria.builder().id(5L).nombre("Abarrotes").activa(true).build();
        var kg = UnidadMedida.builder().id(6L).codigo("KG").nombre("Kilogramos").activa(true).build();
        var rice = Producto.builder().id(10L).nombre("Arroz").categoria(category).unidadMedida(kg)
            .stockMinimo(BigDecimal.ZERO).activo(true).build();
        var oil = Producto.builder().id(11L).nombre("Aceite").categoria(category).unidadMedida(kg)
            .stockMinimo(BigDecimal.ZERO).activo(true).build();

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("solicita", "n/a", List.of()));
        when(areaRepository.findById(2L)).thenReturn(Optional.of(area));
        when(almacenRepository.findById(3L)).thenReturn(Optional.of(warehouse));
        when(estadoRepository.findByNombre(EstadoRequerimientoNombre.PENDIENTE)).thenReturn(Optional.of(state));
        when(usuarioRepository.findByUsername("solicita")).thenReturn(Optional.of(user));
        when(usuarioAreaRepository.existsByUsuarioIdAndAreaId(4L, 2L)).thenReturn(true);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(rice));
        when(productoRepository.findById(11L)).thenReturn(Optional.of(oil));
        when(requerimientoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.crear(new RequerimientoRequest(2L, 3L, "Sin stock mínimo requerido", List.of(
            new DetalleRequerimientoRequest(10L, new BigDecimal("0.250")),
            new DetalleRequerimientoRequest(11L, new BigDecimal("10.000")))));

        assertEquals(2, response.detalles().size());
        assertEquals(new BigDecimal("0.250"), response.detalles().get(0).cantidadSolicitada());
        assertEquals("PENDIENTE", response.estado());
        verify(requerimientoRepository, times(2)).save(any());
    }
}
