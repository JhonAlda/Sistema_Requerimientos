package com.gestionalmacen.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gestionalmacen.dto.request.EntregarDespachoRequest;
import com.gestionalmacen.dto.request.EntregarProductoRequest;
import com.gestionalmacen.entity.Almacen;
import com.gestionalmacen.entity.Categoria;
import com.gestionalmacen.entity.DetalleRequerimiento;
import com.gestionalmacen.entity.Despacho;
import com.gestionalmacen.entity.EstadoRequerimiento;
import com.gestionalmacen.entity.Inventario;
import com.gestionalmacen.entity.Producto;
import com.gestionalmacen.entity.Requerimiento;
import com.gestionalmacen.entity.UnidadMedida;
import com.gestionalmacen.enums.EstadoDespacho;
import com.gestionalmacen.exception.BusinessRuleException;
import com.gestionalmacen.repository.AlmacenRepository;
import com.gestionalmacen.repository.DetalleDespachoRepository;
import com.gestionalmacen.repository.DetalleRequerimientoRepository;
import com.gestionalmacen.repository.DespachoRepository;
import com.gestionalmacen.repository.EstadoRequerimientoRepository;
import com.gestionalmacen.repository.InventarioRepository;
import com.gestionalmacen.repository.MovimientoInventarioRepository;
import com.gestionalmacen.repository.RequerimientoRepository;
import com.gestionalmacen.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DespachoServiceImplTest {
    @Mock private DespachoRepository despachoRepository;
    @Mock private DetalleDespachoRepository detalleDespachoRepository;
    @Mock private RequerimientoRepository requerimientoRepository;
    @Mock private DetalleRequerimientoRepository detalleRequerimientoRepository;
    @Mock private EstadoRequerimientoRepository estadoRepository;
    @Mock private InventarioRepository inventarioRepository;
    @Mock private MovimientoInventarioRepository movimientoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AlmacenRepository almacenRepository;

    @InjectMocks private DespachoServiceImpl service;

    @Test
    void rechazaStockInsuficienteSinGuardarMovimientoNiCambiarInventario() {
        var unit = UnidadMedida.builder().id(1L).codigo("KG").nombre("Kilogramos").build();
        var category = Categoria.builder().id(1L).nombre("Abarrotes").activa(true).build();
        var product = Producto.builder().id(7L).nombre("Arroz").activo(true)
            .unidadMedida(unit).categoria(category).stockMinimo(BigDecimal.ZERO).build();
        var warehouse = Almacen.builder().id(2L).codigo("ALM-01").nombre("Principal").build();
        var request = Requerimiento.builder().id(3L).numero("REQ-TEST-1").almacen(warehouse).build();
        var detail = DetalleRequerimiento.builder().id(4L).requerimiento(request).producto(product)
            .cantidadSolicitada(new BigDecimal("10.000")).cantidadDespachada(BigDecimal.ZERO).build();
        var dispatch = Despacho.builder().id(5L).requerimiento(request).estado(EstadoDespacho.INICIADO).build();
        var inventory = Inventario.builder().id(6L).almacen(warehouse).producto(product)
            .existencia(new BigDecimal("6.000")).build();

        when(despachoRepository.findById(5L)).thenReturn(Optional.of(dispatch));
        when(detalleDespachoRepository.findByDespachoIdAndDetalleRequerimientoId(5L, 4L))
            .thenReturn(Optional.empty());
        when(detalleRequerimientoRepository.findLockedById(4L)).thenReturn(Optional.of(detail));
        when(inventarioRepository.findLockedByAlmacenIdAndProductoId(2L, 7L))
            .thenReturn(Optional.of(inventory));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
            () -> service.entregar(5L, new EntregarDespachoRequest(
                java.util.List.of(new EntregarProductoRequest(4L, new BigDecimal("10.000"))))));

        assertTrue(error.getMessage().contains("Producto: Arroz"));
        assertTrue(error.getMessage().contains("Disponible: 6.000 KG"));
        verify(inventarioRepository, never()).save(inventory);
        verify(movimientoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
