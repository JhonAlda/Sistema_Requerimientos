package com.gestionalmacen.repository;

import com.gestionalmacen.entity.UsuarioArea;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioAreaRepository extends JpaRepository<UsuarioArea, Long> {
    List<UsuarioArea> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndAreaId(Long usuarioId, Long areaId);
    void deleteByUsuarioId(Long usuarioId);
}
