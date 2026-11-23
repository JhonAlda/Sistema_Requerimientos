package com.gestionalmacen.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "requerimientos", uniqueConstraints = @UniqueConstraint(name = "uk_requerimiento_numero", columnNames = "numero"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Requerimiento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoRequerimiento estado;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(columnDefinition = "nvarchar(max)")
    private String observaciones;

    @OneToMany(mappedBy = "requerimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleRequerimiento> detalles = new ArrayList<>();

    @PrePersist
    void asignarFechaSolicitud() {
        if (fechaSolicitud == null) fechaSolicitud = LocalDateTime.now();
    }
}
