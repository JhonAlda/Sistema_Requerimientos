package com.gestionalmacen.repository;
import com.gestionalmacen.entity.DetalleRequerimiento;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetalleRequerimientoRepository extends JpaRepository<DetalleRequerimiento, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT d FROM DetalleRequerimiento d WHERE d.id = :id")
	Optional<DetalleRequerimiento> findLockedById(@Param("id") Long id);
}
