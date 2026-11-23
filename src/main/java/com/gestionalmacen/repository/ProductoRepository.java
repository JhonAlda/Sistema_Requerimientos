package com.gestionalmacen.repository;

import com.gestionalmacen.entity.Producto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findBySku(String sku);

    @Query("""
        SELECT p FROM Producto p
        WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
          AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
        """)
    Page<Producto> buscar(@Param("nombre") String nombre,
                          @Param("categoriaId") Long categoriaId,
                          Pageable pageable);
}
