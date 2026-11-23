package com.gestionalmacen.entity;

import com.gestionalmacen.enums.EstadoRequerimientoNombre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_requerimiento", uniqueConstraints = @UniqueConstraint(name = "uk_estado_requerimiento_nombre", columnNames = "nombre"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoRequerimiento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoRequerimientoNombre nombre;
}
