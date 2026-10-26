package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "ejemplares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE ejemplares SET deleted = true WHERE id = ?")

public class EjemplarEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "especie_id", nullable = false)
    private EspecieEntity especie;

    @Enumerated(EnumType.STRING)
    @Column(name = "etapa_crecimiento", nullable = false)
    private EtapaCrecimiento etapaCrecimiento;

    @Embedded
    private PrecioEmbeddable precio;

    @Column(name = "disponibilidad", nullable = false)
    private boolean disponibilidad = true;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
