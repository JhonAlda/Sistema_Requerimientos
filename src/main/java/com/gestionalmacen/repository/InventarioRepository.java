package com.gestionalmacen.repository;

import com.gestionalmacen.entity.Inventario;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByAlmacenId(Long almacenId);
    Optional<Inventario> findByAlmacenIdAndProductoId(Long almacenId, Long productoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventario i WHERE i.almacen.id = :almacenId AND i.producto.id = :productoId")
    Optional<Inventario> findLockedByAlmacenIdAndProductoId(@Param("almacenId") Long almacenId,
                                                           @Param("productoId") Long productoId);
}
