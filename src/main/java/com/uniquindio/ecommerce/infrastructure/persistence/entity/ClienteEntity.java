package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE clientes SET deleted = true WHERE id = ?")

public class ClienteEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "email"))
    private EmailEmbeddable email;

    @Column(name = "telefono")
    private String telefono;

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

    @Column(name = "vivero_asignado_id", length = 36)
    private String viveroAsignadoId;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
