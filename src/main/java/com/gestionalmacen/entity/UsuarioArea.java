package com.gestionalmacen.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_areas", uniqueConstraints = @UniqueConstraint(name = "uk_usuario_area", columnNames = {"usuario_id", "area_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioArea {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;
}
