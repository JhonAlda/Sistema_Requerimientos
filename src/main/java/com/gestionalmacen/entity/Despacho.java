package com.gestionalmacen.entity;

import com.gestionalmacen.enums.EstadoDespacho;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "despachos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Despacho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requerimiento_id", nullable = false)
    private Requerimiento requerimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "despachador_id", nullable = false)
    private Usuario despachador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoDespacho estado = EstadoDespacho.INICIADO;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleDespacho> detalles = new ArrayList<>();

    @PrePersist
    void asignarInicio() {
        if (fechaInicio == null) fechaInicio = LocalDateTime.now();
    }
}
