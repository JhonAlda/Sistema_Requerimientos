package com.gestionalmacen.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "detalle_despachos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleDespacho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "despacho_id", nullable = false)
    private Despacho despacho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "detalle_requerimiento_id", nullable = false)
    private DetalleRequerimiento detalleRequerimiento;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal cantidadEntregada;
}
