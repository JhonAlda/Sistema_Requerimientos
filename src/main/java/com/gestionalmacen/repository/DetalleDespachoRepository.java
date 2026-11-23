package com.gestionalmacen.repository;

import com.gestionalmacen.entity.DetalleDespacho;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleDespachoRepository extends JpaRepository<DetalleDespacho, Long> {
    Optional<DetalleDespacho> findByDespachoIdAndDetalleRequerimientoId(Long despachoId, Long detalleRequerimientoId);
}
