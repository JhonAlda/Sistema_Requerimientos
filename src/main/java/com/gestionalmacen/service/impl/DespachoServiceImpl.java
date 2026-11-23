package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.EntregarDespachoRequest;
import com.gestionalmacen.dto.request.ConfirmarDespachoRequest;
import com.gestionalmacen.dto.request.IniciarDespachoRequest;
import com.gestionalmacen.dto.response.DetalleDespachoResponse;
import com.gestionalmacen.dto.response.DespachoResponse;
import com.gestionalmacen.entity.*;
import com.gestionalmacen.enums.EstadoDespacho;
import com.gestionalmacen.enums.EstadoRequerimientoNombre;
import com.gestionalmacen.enums.TipoMovimiento;
import com.gestionalmacen.exception.BusinessRuleException;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.repository.*;
import com.gestionalmacen.service.interfaces.DespachoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DespachoServiceImpl implements DespachoService {
    private final DespachoRepository despachoRepository;
    private final DetalleDespachoRepository detalleDespachoRepository;
    private final RequerimientoRepository requerimientoRepository;
    private final DetalleRequerimientoRepository detalleRequerimientoRepository;
    private final EstadoRequerimientoRepository estadoRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    public DespachoServiceImpl(DespachoRepository despachoRepository,
                               DetalleDespachoRepository detalleDespachoRepository,
                               RequerimientoRepository requerimientoRepository,
                               DetalleRequerimientoRepository detalleRequerimientoRepository,
                               EstadoRequerimientoRepository estadoRepository,
                               InventarioRepository inventarioRepository,
                               MovimientoInventarioRepository movimientoRepository,
                               UsuarioRepository usuarioRepository) {
        this.despachoRepository = despachoRepository;
        this.detalleDespachoRepository = detalleDespachoRepository;
        this.requerimientoRepository = requerimientoRepository;
        this.detalleRequerimientoRepository = detalleRequerimientoRepository;
        this.estadoRepository = estadoRepository;
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public DespachoResponse confirmar(ConfirmarDespachoRequest request) {
        DespachoResponse iniciado = iniciar(new IniciarDespachoRequest(request.requerimientoId()));
        entregar(iniciado.id(), new EntregarDespachoRequest(request.entregas()));
        return finalizar(iniciado.id());
    }

    @Override
    public DespachoResponse iniciar(IniciarDespachoRequest request) {
        Requerimiento requerimiento = buscarRequerimiento(request.requerimientoId());
        if (requerimiento.getEstado().getNombre() == EstadoRequerimientoNombre.ATENDIDO
            || requerimiento.getEstado().getNombre() == EstadoRequerimientoNombre.CANCELADO) {
            throw new BusinessRuleException("El requerimiento no admite nuevos despachos");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario despachador = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario despachador no encontrado"));

        Despacho despacho = Despacho.builder()
            .requerimiento(requerimiento)
            .despachador(despachador)
            .estado(EstadoDespacho.INICIADO)
            .build();
        despacho = despachoRepository.save(despacho);
        cambiarEstado(requerimiento, EstadoRequerimientoNombre.EN_PROCESO);
        requerimientoRepository.save(requerimiento);
        return toResponse(despacho);
    }

    @Override
    public DespachoResponse entregar(Long despachoId, EntregarDespachoRequest request) {
        Despacho despacho = buscar(despachoId);
        if (despacho.getEstado() != EstadoDespacho.INICIADO) {
            throw new BusinessRuleException("El despacho no está iniciado");
        }

        Requerimiento requerimiento = despacho.getRequerimiento();
        Set<Long> procesados = new HashSet<>();

        for (var entrega : request.entregas()) {
            if (!procesados.add(entrega.detalleRequerimientoId())) {
                throw new BusinessRuleException("El detalle de requerimiento está repetido en esta entrega");
            }
            if (detalleDespachoRepository.findByDespachoIdAndDetalleRequerimientoId(
                despachoId, entrega.detalleRequerimientoId()).isPresent()) {
                throw new BusinessRuleException("La línea ya fue entregada en este despacho");
            }

            DetalleRequerimiento detalle = detalleRequerimientoRepository.findLockedById(entrega.detalleRequerimientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de requerimiento no encontrado: "
                    + entrega.detalleRequerimientoId()));
            if (!detalle.getRequerimiento().getId().equals(requerimiento.getId())) {
                throw new BusinessRuleException("El detalle no pertenece al requerimiento del despacho");
            }
            if (!Boolean.TRUE.equals(detalle.getProducto().getActivo())) {
                throw new BusinessRuleException("El producto " + detalle.getProducto().getNombre() + " está inactivo y no se puede despachar");
            }

            BigDecimal pendiente = detalle.getCantidadSolicitada().subtract(detalle.getCantidadDespachada());
            if (entrega.cantidadEntregada().compareTo(pendiente) > 0) {
                throw new BusinessRuleException("La cantidad entregada supera el pendiente del producto "
                    + detalle.getProducto().getNombre());
            }

            Inventario inventario = inventarioRepository
                .findLockedByAlmacenIdAndProductoId(requerimiento.getAlmacen().getId(), detalle.getProducto().getId())
                .orElseThrow(() -> new BusinessRuleException("No existe inventario para el producto "
                    + detalle.getProducto().getNombre()));
            if (inventario.getExistencia().compareTo(entrega.cantidadEntregada()) < 0) {
                throw new BusinessRuleException("Stock insuficiente. Producto: " + detalle.getProducto().getNombre()
                    + ". Solicitado en esta entrega: " + entrega.cantidadEntregada() + " "
                    + detalle.getProducto().getUnidadMedida().getCodigo() + ". Disponible: " + inventario.getExistencia()
                    + " " + detalle.getProducto().getUnidadMedida().getCodigo()
                    + ". Puedes reducir la entrega y dejar el resto pendiente.");
            }

            BigDecimal saldo = inventario.getExistencia().subtract(entrega.cantidadEntregada());
            inventario.setExistencia(saldo);
            inventarioRepository.save(inventario);

            MovimientoInventario movimiento = MovimientoInventario.builder()
                .inventario(inventario)
                .tipo(TipoMovimiento.SALIDA)
                .cantidad(entrega.cantidadEntregada())
                .saldoResultante(saldo)
                .usuario(despachadorActual())
                .referencia("DESP-" + despachoId)
                .observacion("Despacho de requerimiento " + requerimiento.getNumero())
                .build();
            movimientoRepository.save(movimiento);

            DetalleDespacho detalleDespacho = DetalleDespacho.builder()
                .despacho(despacho)
                .detalleRequerimiento(detalle)
                .cantidadEntregada(entrega.cantidadEntregada())
                .build();
            despacho.getDetalles().add(detalleDespacho);

            detalle.setCantidadDespachada(detalle.getCantidadDespachada().add(entrega.cantidadEntregada()));
            detalleRequerimientoRepository.save(detalle);
        }

        boolean completo = requerimiento.getDetalles().stream()
            .allMatch(d -> d.getCantidadDespachada().compareTo(d.getCantidadSolicitada()) == 0);
        cambiarEstado(requerimiento, completo
            ? EstadoRequerimientoNombre.ATENDIDO
            : EstadoRequerimientoNombre.PARCIAL);
        requerimientoRepository.save(requerimiento);
        despacho = despachoRepository.save(despacho);
        return toResponse(despacho);
    }

    @Override
    public DespachoResponse finalizar(Long despachoId) {
        Despacho despacho = buscar(despachoId);
        if (despacho.getEstado() == EstadoDespacho.FINALIZADO) {
            throw new BusinessRuleException("El despacho ya fue finalizado");
        }
        if (despacho.getDetalles().isEmpty()) {
            throw new BusinessRuleException("No se puede finalizar un despacho sin entregas");
        }

        despacho.setFechaFin(LocalDateTime.now());
        despacho.setEstado(EstadoDespacho.FINALIZADO);
        despacho = despachoRepository.save(despacho);

        Requerimiento requerimiento = despacho.getRequerimiento();
        boolean completo = requerimiento.getDetalles().stream()
            .allMatch(d -> d.getCantidadDespachada().compareTo(d.getCantidadSolicitada()) == 0);
        cambiarEstado(requerimiento, completo
            ? EstadoRequerimientoNombre.ATENDIDO
            : EstadoRequerimientoNombre.PARCIAL);
        requerimientoRepository.save(requerimiento);
        return toResponse(despacho);
    }

    @Override @Transactional(readOnly = true)
    public DespachoResponse obtener(Long despachoId) {
        return toResponse(buscar(despachoId));
    }

    private Usuario despachadorActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario despachador no encontrado"));
    }

    private Requerimiento buscarRequerimiento(Long id) {
        return requerimientoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Requerimiento no encontrado: " + id));
    }

    private Despacho buscar(Long id) {
        return despachoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado: " + id));
    }

    private void cambiarEstado(Requerimiento requerimiento, EstadoRequerimientoNombre nombre) {
        EstadoRequerimiento estado = estadoRepository.findByNombre(nombre)
            .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado: " + nombre));
        requerimiento.setEstado(estado);
    }

    private DespachoResponse toResponse(Despacho d) {
        var detalles = d.getDetalles().stream().map(dd -> new DetalleDespachoResponse(
            dd.getId(), dd.getDetalleRequerimiento().getId(), dd.getDetalleRequerimiento().getProducto().getId(),
            dd.getDetalleRequerimiento().getProducto().getNombre(), dd.getCantidadEntregada())).toList();
        return new DespachoResponse(d.getId(), d.getRequerimiento().getId(), d.getRequerimiento().getNumero(),
            d.getDespachador().getId(), d.getDespachador().getNombres(), d.getEstado().name(),
            d.getFechaInicio(), d.getFechaFin(), detalles);
    }
}
