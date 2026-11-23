package com.gestionalmacen.repository;

import com.gestionalmacen.entity.Usuario;
import com.gestionalmacen.enums.NombreRol;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = {"rol", "sede"})
    Optional<Usuario> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre = :rol AND u.activo = true")
    long countActivosPorRol(@Param("rol") NombreRol rol);
}
