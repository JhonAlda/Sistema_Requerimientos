package com.gestionalmacen.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sedes", uniqueConstraints = @UniqueConstraint(name = "uk_sede_nombre", columnNames = "nombre"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sede {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 255)
    private String direccion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;
}
