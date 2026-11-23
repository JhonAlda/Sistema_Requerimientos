package com.gestionalmacen.repository;

import com.gestionalmacen.entity.EstadoRequerimiento;
import com.gestionalmacen.enums.EstadoRequerimientoNombre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRequerimientoRepository extends JpaRepository<EstadoRequerimiento, Long> {
    Optional<EstadoRequerimiento> findByNombre(EstadoRequerimientoNombre nombre);
}
