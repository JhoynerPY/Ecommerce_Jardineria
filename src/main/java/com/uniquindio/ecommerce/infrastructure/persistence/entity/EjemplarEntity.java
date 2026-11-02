package com.uniquindio.ecommerce.infrastructure.persistence.entity;


import jakarta.persistence.*;
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
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "precio_valor")),
            @AttributeOverride(name = "moneda", column = @Column(name = "precio_moneda"))
    })
    private PrecioEmbeddable precio;

    @Column(name = "disponibilidad", nullable = false)
    private boolean disponibilidad = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_salud", nullable = false)
    private EstadoDeSalud estadoSalud = EstadoDeSalud.SANO;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
