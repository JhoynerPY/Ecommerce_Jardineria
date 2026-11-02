package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "articulos_kit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE articulos_kit SET deleted = true WHERE id = ?")

public class ArticuloKitEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "precio_valor")),
            @AttributeOverride(name = "moneda", column = @Column(name = "precio_moneda"))
    })
    private PrecioEmbeddable precio;

    @Column(name = "diametro", nullable = false)
    private double diametro;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
