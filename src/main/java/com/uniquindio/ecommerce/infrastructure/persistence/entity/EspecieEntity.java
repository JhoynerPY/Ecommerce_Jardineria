package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "especies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE especies SET deleted = true WHERE id = ?")

public class EspecieEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nombre_comun", nullable = false)
    private String nombreComun;

    @Column(name = "nombre_cientifico", nullable = false)
    private String nombreCientifico;

    @Embedded
    private FichaDeCuidadoEmbeddable fichaDeCuidado;

    @Embedded
    private RiesgoEmbeddable riesgo;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
