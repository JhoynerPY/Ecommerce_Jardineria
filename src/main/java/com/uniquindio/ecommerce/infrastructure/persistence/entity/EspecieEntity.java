package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
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
    @AttributeOverrides({
            @AttributeOverride(name = "requerimientoLuz", column = @Column(name = "ficha_de_cuidado_requerimiento_luz")),
            @AttributeOverride(name = "frecuenciaRiego", column = @Column(name = "ficha_de_cuidado_frecuencia_riego")),
            @AttributeOverride(name = "sustrato", column = @Column(name = "ficha_de_cuidado_sustrato")),
            @AttributeOverride(name = "temporadaFloracion", column = @Column(name = "ficha_de_cuidado_temporada_floracion"))
    })
    private FichaDeCuidadoEmbeddable fichaDeCuidado;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "nivel", column = @Column(name = "riesgo_nivel")),
            @AttributeOverride(name = "descripcion", column = @Column(name = "riesgo_descripcion"))
    })
    private RiesgoEmbeddable riesgo;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
