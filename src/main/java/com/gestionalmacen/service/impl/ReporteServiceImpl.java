package com.gestionalmacen.service.impl;

import com.gestionalmacen.dto.response.DashboardResponse;
import com.gestionalmacen.dto.response.ReporteCantidadResponse;
import com.gestionalmacen.dto.response.ReporteTiempoResponse;
import com.gestionalmacen.entity.DetalleDespacho;
import com.gestionalmacen.entity.DetalleRequerimiento;
import com.gestionalmacen.entity.Despacho;
import com.gestionalmacen.entity.Requerimiento;
import com.gestionalmacen.enums.EstadoDespacho;
import com.gestionalmacen.enums.EstadoRequerimientoNombre;
import com.gestionalmacen.repository.DespachoRepository;
import com.gestionalmacen.repository.InventarioRepository;
import com.gestionalmacen.repository.RequerimientoRepository;
import com.gestionalmacen.service.interfaces.ReporteService;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {
    private final RequerimientoRepository requerimientoRepository;
    private final DespachoRepository despachoRepository;
    private final InventarioRepository inventarioRepository;

    public ReporteServiceImpl(RequerimientoRepository requerimientoRepository,
                              DespachoRepository despachoRepository,
                              InventarioRepository inventarioRepository) {
        this.requerimientoRepository = requerimientoRepository;
        this.despachoRepository = despachoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public DashboardResponse dashboard() {
        List<Requerimiento> requerimientos = requerimientoRepository.findAll();
        List<Despacho> despachos = despachoRepository.findAll();
        long pendientes = requerimientos.stream()
            .filter(r -> r.getEstado().getNombre() == EstadoRequerimientoNombre.PENDIENTE).count();
        long parciales = requerimientos.stream()
            .filter(r -> r.getEstado().getNombre() == EstadoRequerimientoNombre.PARCIAL).count();
        long iniciados = despachos.stream()
            .filter(d -> d.getEstado() == EstadoDespacho.INICIADO).count();
        long stockBajo = inventarioRepository.findAll().stream()
            .filter(i -> i.getExistencia().compareTo(i.getProducto().getStockMinimo()) <= 0).count();
        BigDecimal pendientesPorDespachar = requerimientos.stream()
            .flatMap(r -> r.getDetalles().stream())
            .map(d -> d.getCantidadSolicitada().subtract(d.getCantidadDespachada()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new DashboardResponse(pendientes, parciales, iniciados, stockBajo, pendientesPorDespachar);
    }

    @Override
    public List<ReporteCantidadResponse> consumoPorArea() {
        Map<String, BigDecimal> datos = new LinkedHashMap<>();
        for (Requerimiento requerimiento : requerimientoRepository.findAll()) {
            String area = requerimiento.getArea().getNombre();
            BigDecimal total = requerimiento.getDetalles().stream()
                .map(DetalleRequerimiento::getCantidadDespachada)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            datos.merge(area, total, BigDecimal::add);
        }
        return datos.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder()))
            .map(e -> new ReporteCantidadResponse(e.getKey(), e.getValue())).toList();
    }

    @Override
    public List<ReporteCantidadResponse> productosMasSolicitados() {
        Map<String, BigDecimal> datos = new LinkedHashMap<>();
        requerimientoRepository.findAll().stream()
            .flatMap(r -> r.getDetalles().stream())
            .forEach(d -> datos.merge(d.getProducto().getNombre(), d.getCantidadSolicitada(), BigDecimal::add));
        return ordenar(datos);
    }

    @Override
    public List<ReporteCantidadResponse> productosMasDespachados() {
        Map<String, BigDecimal> datos = new LinkedHashMap<>();
        despachoRepository.findAll().stream()
            .flatMap(d -> d.getDetalles().stream())
            .forEach(d -> datos.merge(d.getDetalleRequerimiento().getProducto().getNombre(),
                d.getCantidadEntregada(), BigDecimal::add));
        return ordenar(datos);
    }

    @Override
    public List<ReporteCantidadResponse> cantidadesPendientes() {
        Map<String, BigDecimal> datos = new LinkedHashMap<>();
        requerimientoRepository.findAll().stream()
            .flatMap(r -> r.getDetalles().stream())
            .forEach(d -> datos.merge(d.getProducto().getNombre(),
                d.getCantidadSolicitada().subtract(d.getCantidadDespachada()), BigDecimal::add));
        return ordenar(datos);
    }

    @Override
    public ReporteTiempoResponse tiemposAtencion() {
        List<Despacho> finalizados = despachoRepository.findAll().stream()
            .filter(d -> d.getEstado() == EstadoDespacho.FINALIZADO && d.getFechaFin() != null)
            .toList();

        List<Double> esperas = new ArrayList<>();
        List<Double> preparaciones = new ArrayList<>();
        List<Double> totales = new ArrayList<>();
        for (Despacho despacho : finalizados) {
            esperas.add((double) Duration.between(despacho.getRequerimiento().getFechaSolicitud(), despacho.getFechaInicio()).toMinutes());
            preparaciones.add((double) Duration.between(despacho.getFechaInicio(), despacho.getFechaFin()).toMinutes());
            totales.add((double) Duration.between(despacho.getRequerimiento().getFechaSolicitud(), despacho.getFechaFin()).toMinutes());
        }

        List<String> alertas = finalizados.stream()
            .filter(d -> Duration.between(d.getRequerimiento().getFechaSolicitud(), d.getFechaFin()).toMinutes() > 120)
            .map(d -> "Requerimiento " + d.getRequerimiento().getNumero() + " superó 120 minutos")
            .toList();

        return new ReporteTiempoResponse(promedio(esperas), promedio(preparaciones), promedio(totales), alertas);
    }

    private List<ReporteCantidadResponse> ordenar(Map<String, BigDecimal> datos) {
        return datos.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder()))
            .limit(20)
            .map(e -> new ReporteCantidadResponse(e.getKey(), e.getValue()))
            .toList();
    }

    private double promedio(List<Double> valores) {
        return valores.isEmpty() ? 0 : valores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
}
