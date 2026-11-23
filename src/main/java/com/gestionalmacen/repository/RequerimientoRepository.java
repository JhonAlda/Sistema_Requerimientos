package com.gestionalmacen.repository;

import com.gestionalmacen.entity.Requerimiento;
import com.gestionalmacen.enums.EstadoRequerimientoNombre;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequerimientoRepository extends JpaRepository<Requerimiento, Long> {
    List<Requerimiento> findByEstadoNombre(com.gestionalmacen.enums.EstadoRequerimientoNombre nombre);
    List<Requerimiento> findByEstadoNombreIn(List<EstadoRequerimientoNombre> nombres);
    List<Requerimiento> findByAreaId(Long areaId);
    List<Requerimiento> findByAreaIdIn(List<Long> areaIds);
}
