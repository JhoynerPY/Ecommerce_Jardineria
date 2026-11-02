package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "viveros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE viveros SET deleted = true WHERE id = ?")

public class ViveroEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "direccion_calle")),
            @AttributeOverride(name = "ciudad", column = @Column(name = "direccion_ciudad")),
            @AttributeOverride(name = "departamento", column = @Column(name = "direccion_departamento")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "direccion_codigo_postal")),
            @AttributeOverride(name = "pais", column = @Column(name = "direccion_pais")),
            @AttributeOverride(name = "latitud", column = @Column(name = "direccion_latitud")),
            @AttributeOverride(name = "longitud", column = @Column(name = "direccion_longitud"))
    })
    private DireccionEmbeddable direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "radio_cobertura_km", nullable = false)
    private double radioCoberturaKm;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
