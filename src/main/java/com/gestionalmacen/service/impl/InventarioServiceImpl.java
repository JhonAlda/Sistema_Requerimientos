package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.AjusteInventarioRequest;
import com.gestionalmacen.dto.request.MovimientoInventarioRequest;
import com.gestionalmacen.dto.response.InventarioResponse;
import com.gestionalmacen.dto.response.MovimientoInventarioResponse;
import com.gestionalmacen.entity.*;
import com.gestionalmacen.enums.TipoMovimiento;
import com.gestionalmacen.exception.BusinessRuleException;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.repository.*;
import com.gestionalmacen.service.interfaces.InventarioService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventarioServiceImpl implements InventarioService {
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final AlmacenRepository almacenRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository,
                                 MovimientoInventarioRepository movimientoRepository,
                                 AlmacenRepository almacenRepository,
                                 ProductoRepository productoRepository,
                                 UsuarioRepository usuarioRepository) {
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.almacenRepository = almacenRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override @Transactional(readOnly = true)
    public List<InventarioResponse> listar() {
        return inventarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<InventarioResponse> listarPorAlmacen(Long almacenId) {
        return inventarioRepository.findByAlmacenId(almacenId).stream().map(this::toResponse).toList();
    }

    @Override
    public MovimientoInventarioResponse entrada(MovimientoInventarioRequest request) {
        Almacen almacen = buscarAlmacen(request.almacenId());
        Producto producto = buscarProducto(request.productoId());
        Inventario inventario = inventarioRepository
            .findLockedByAlmacenIdAndProductoId(almacen.getId(), producto.getId())
            .orElseGet(() -> Inventario.builder()
                .almacen(almacen).producto(producto).existencia(BigDecimal.ZERO).build());

        BigDecimal nuevoSaldo = inventario.getExistencia().add(request.cantidad());
        inventario.setExistencia(nuevoSaldo);
        inventario = inventarioRepository.save(inventario);

        MovimientoInventario movimiento = MovimientoInventario.builder()
            .inventario(inventario)
            .tipo(TipoMovimiento.ENTRADA)
            .cantidad(request.cantidad())
            .saldoResultante(nuevoSaldo)
            .usuario(usuarioActual())
            .referencia(request.referencia())
            .observacion(request.observacion())
            .build();
        movimiento = movimientoRepository.save(movimiento);
        return toMovementResponse(movimiento);
    }

    @Override
    public MovimientoInventarioResponse ajuste(AjusteInventarioRequest request) {
        TipoMovimiento tipo;
        try {
            tipo = TipoMovimiento.valueOf(request.tipo().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Tipo de ajuste inválido. Use AJUSTE_POSITIVO o AJUSTE_NEGATIVO");
        }
        if (tipo != TipoMovimiento.AJUSTE_POSITIVO && tipo != TipoMovimiento.AJUSTE_NEGATIVO) {
            throw new BusinessRuleException("Tipo de ajuste inválido");
        }

        Inventario inventario = inventarioRepository
            .findLockedByAlmacenIdAndProductoId(request.almacenId(), request.productoId())
            .orElseThrow(() -> new BusinessRuleException("No existe inventario para el almacén y producto indicados"));

        BigDecimal nuevoSaldo = tipo == TipoMovimiento.AJUSTE_POSITIVO
            ? inventario.getExistencia().add(request.cantidad())
            : inventario.getExistencia().subtract(request.cantidad());
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("El ajuste dejaría existencia negativa");
        }

        inventario.setExistencia(nuevoSaldo);
        inventario = inventarioRepository.save(inventario);
        MovimientoInventario movimiento = MovimientoInventario.builder()
            .inventario(inventario)
            .tipo(tipo)
            .cantidad(request.cantidad())
            .saldoResultante(nuevoSaldo)
            .usuario(usuarioActual())
            .referencia(request.referencia())
            .observacion(request.observacion())
            .build();
        movimiento = movimientoRepository.save(movimiento);
        return toMovementResponse(movimiento);
    }

    private Almacen buscarAlmacen(Long id) {
        return almacenRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado: " + id));
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
    }

    private Usuario usuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private InventarioResponse toResponse(Inventario i) {
        BigDecimal minimo = i.getProducto().getStockMinimo();
        return new InventarioResponse(i.getId(), i.getAlmacen().getId(), i.getAlmacen().getNombre(),
            i.getProducto().getId(), i.getProducto().getNombre(), i.getProducto().getUnidadMedida().getCodigo(),
            i.getExistencia(), minimo, i.getExistencia().compareTo(minimo) <= 0);
    }

    private MovimientoInventarioResponse toMovementResponse(MovimientoInventario m) {
        return new MovimientoInventarioResponse(m.getId(), m.getTipo().name(),
            m.getInventario().getAlmacen().getId(), m.getInventario().getProducto().getId(),
            m.getCantidad(), m.getSaldoResultante(), m.getFecha(), m.getReferencia(), m.getObservacion());
    }
}
