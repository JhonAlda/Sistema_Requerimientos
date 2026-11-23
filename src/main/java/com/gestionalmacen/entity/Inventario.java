package com.gestionalmacen.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "inventarios", uniqueConstraints = @UniqueConstraint(name = "uk_inventario_almacen_producto", columnNames = {
        "almacen_id", "producto_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 14, scale = 3)
    @Builder.Default
    private BigDecimal existencia = BigDecimal.ZERO;

    @Version
    @Column(nullable = false)
    private Long version;
}
