package com.gestionalmacen.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "auditoria")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Auditoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(name = "entidad_nombre", nullable = false, length = 100)
    private String entidadNombre;

    @Column(name = "entidad_id", length = 100)
    private String entidadId;

    @Column(columnDefinition = "nvarchar(max)")
    private String detalle;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    void asignarFecha() {
        if (fecha == null) fecha = LocalDateTime.now();
    }
}
