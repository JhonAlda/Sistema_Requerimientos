package com.gestionalmacen.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "detalle_requerimientos", uniqueConstraints = @UniqueConstraint(name = "uk_detalle_requerimiento_producto", columnNames = {"requerimiento_id", "producto_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleRequerimiento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requerimiento_id", nullable = false)
    private Requerimiento requerimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_solicitada", nullable = false, precision = 14, scale = 3)
    private BigDecimal cantidadSolicitada;

    @Column(name = "cantidad_despachada", nullable = false, precision = 14, scale = 3)
    @Builder.Default
    private BigDecimal cantidadDespachada = BigDecimal.ZERO;
}
