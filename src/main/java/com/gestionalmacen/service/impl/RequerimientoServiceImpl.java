package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.request.RequerimientoRequest;
import com.gestionalmacen.dto.response.DetalleRequerimientoResponse;
import com.gestionalmacen.dto.response.RequerimientoResponse;
import com.gestionalmacen.entity.*;
import com.gestionalmacen.enums.EstadoRequerimientoNombre;
import com.gestionalmacen.exception.BusinessRuleException;
import com.gestionalmacen.exception.ResourceNotFoundException;
import com.gestionalmacen.repository.*;
import com.gestionalmacen.service.interfaces.RequerimientoService;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RequerimientoServiceImpl implements RequerimientoService {
    private final RequerimientoRepository requerimientoRepository;
    private final DetalleRequerimientoRepository detalleRepository;
    private final EstadoRequerimientoRepository estadoRepository;
    private final AreaRepository areaRepository;
    private final AlmacenRepository almacenRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAreaRepository usuarioAreaRepository;

    public RequerimientoServiceImpl(RequerimientoRepository requerimientoRepository,
                                    DetalleRequerimientoRepository detalleRepository,
                                    EstadoRequerimientoRepository estadoRepository,
                                    AreaRepository areaRepository,
                                    AlmacenRepository almacenRepository,
                                    ProductoRepository productoRepository,
                                    UsuarioRepository usuarioRepository,
                                    UsuarioAreaRepository usuarioAreaRepository) {
        this.requerimientoRepository = requerimientoRepository;
        this.detalleRepository = detalleRepository;
        this.estadoRepository = estadoRepository;
        this.areaRepository = areaRepository;
        this.almacenRepository = almacenRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioAreaRepository = usuarioAreaRepository;
    }

    @Override
    public RequerimientoResponse crear(RequerimientoRequest request) {
        Set<Long> productoIds = new HashSet<>();
        for (var detalle : request.detalles()) {
            if (!productoIds.add(detalle.productoId())) {
                throw new BusinessRuleException("No se puede repetir el producto " + detalle.productoId());
            }
        }

        Area area = areaRepository.findById(request.areaId())
            .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada: " + request.areaId()));
        Almacen almacen = almacenRepository.findById(request.almacenId())
            .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado: " + request.almacenId()));
        EstadoRequerimiento estado = estadoRepository.findByNombre(EstadoRequerimientoNombre.PENDIENTE)
            .orElseThrow(() -> new ResourceNotFoundException("Estado PENDIENTE no encontrado"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario solicitante = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario solicitante no encontrado"));
        validarAccesoArea(solicitante, area.getId());

        List<Producto> productos = new ArrayList<>();
        for (var detalleRequest : request.detalles()) {
            Producto producto = productoRepository.findById(detalleRequest.productoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + detalleRequest.productoId()));
            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new BusinessRuleException("El producto " + producto.getNombre() + " está inactivo y no se puede agregar");
            }
            productos.add(producto);
        }

        long siguiente = requerimientoRepository.count() + 1;
        Requerimiento requerimiento = Requerimiento.builder()
            .numero("REQ-" + Year.now().getValue() + "-" + String.format("%06d", siguiente))
            .area(area)
            .almacen(almacen)
            .solicitante(solicitante)
            .estado(estado)
            .observaciones(request.observaciones())
            .build();
        requerimiento = requerimientoRepository.save(requerimiento);

        for (int index = 0; index < request.detalles().size(); index++) {
            var detalleRequest = request.detalles().get(index);
            Producto producto = productos.get(index);
            DetalleRequerimiento detalle = DetalleRequerimiento.builder()
                .requerimiento(requerimiento)
                .producto(producto)
                .cantidadSolicitada(detalleRequest.cantidad())
                .cantidadDespachada(java.math.BigDecimal.ZERO)
                .build();
            requerimiento.getDetalles().add(detalle);
        }
        requerimiento = requerimientoRepository.save(requerimiento);
        return toResponse(requerimiento);
    }

    @Override @Transactional(readOnly = true)
    public RequerimientoResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Override @Transactional(readOnly = true)
    public List<RequerimientoResponse> listarTodos() {
        Usuario usuario = usuarioActual();
        List<Requerimiento> requerimientos = usuario.getRol().getNombre() == com.gestionalmacen.enums.NombreRol.SOLICITANTE
            ? requerimientoRepository.findByAreaIdIn(areasAsignadas(usuario.getId()))
            : requerimientoRepository.findAll();
        return requerimientos.stream().map(this::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<RequerimientoResponse> listarPendientes() {
        List<Requerimiento> requerimientos = requerimientoRepository.findByEstadoNombre(EstadoRequerimientoNombre.PENDIENTE);
        return filtrarPorAreas(requerimientos)
            .stream().map(this::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<RequerimientoResponse> listarParaDespacho() {
        return requerimientoRepository.findByEstadoNombreIn(List.of(
                EstadoRequerimientoNombre.PENDIENTE,
                EstadoRequerimientoNombre.EN_PROCESO,
                EstadoRequerimientoNombre.PARCIAL))
            .stream().map(this::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<RequerimientoResponse> listarPorArea(Long areaId) {
        validarAccesoArea(usuarioActual(), areaId);
        return requerimientoRepository.findByAreaId(areaId)
            .stream().map(this::toResponse).toList();
    }

    private Requerimiento buscar(Long id) {
        Requerimiento requerimiento = requerimientoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Requerimiento no encontrado: " + id));
        validarAccesoArea(usuarioActual(), requerimiento.getArea().getId());
        return requerimiento;
    }

    private Usuario usuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private void validarAccesoArea(Usuario usuario, Long areaId) {
        if (usuario.getRol().getNombre() == com.gestionalmacen.enums.NombreRol.SOLICITANTE
                && !usuarioAreaRepository.existsByUsuarioIdAndAreaId(usuario.getId(), areaId)) {
            throw new AccessDeniedException("No tienes acceso a los requerimientos de esta área.");
        }
    }

    private List<Long> areasAsignadas(Long usuarioId) {
        return usuarioAreaRepository.findByUsuarioId(usuarioId).stream()
            .map(usuarioArea -> usuarioArea.getArea().getId()).toList();
    }

    private List<Requerimiento> filtrarPorAreas(List<Requerimiento> requerimientos) {
        Usuario usuario = usuarioActual();
        if (usuario.getRol().getNombre() != com.gestionalmacen.enums.NombreRol.SOLICITANTE) return requerimientos;
        Set<Long> areas = Set.copyOf(areasAsignadas(usuario.getId()));
        return requerimientos.stream().filter(req -> areas.contains(req.getArea().getId())).toList();
    }

    private RequerimientoResponse toResponse(Requerimiento r) {
        List<DetalleRequerimientoResponse> detalles = r.getDetalles().stream().map(d ->
            new DetalleRequerimientoResponse(d.getId(), d.getProducto().getId(), d.getProducto().getNombre(),
                d.getProducto().getCategoria().getNombre(), d.getProducto().getUnidadMedida().getCodigo(),
                d.getCantidadSolicitada(), d.getCantidadDespachada(),
                d.getCantidadSolicitada().subtract(d.getCantidadDespachada()))
        ).toList();
        return new RequerimientoResponse(r.getId(), r.getNumero(), r.getArea().getId(), r.getArea().getNombre(),
            r.getAlmacen().getId(), r.getAlmacen().getNombre(), r.getSolicitante().getId(), r.getSolicitante().getNombres(),
            r.getEstado().getNombre().name(), r.getFechaSolicitud(), r.getObservaciones(), detalles);
    }
}
