package com.gestionalmacen.repository;

import com.gestionalmacen.entity.Despacho;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    List<Despacho> findByRequerimientoId(Long requerimientoId);
}
