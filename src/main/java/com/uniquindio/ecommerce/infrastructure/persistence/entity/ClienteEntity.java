package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private EmailEmbeddable email;

    @Column(name = "telefono")
    private String telefono;

    @Embedded
    private DireccionEmbeddable direccion;

    @Column(name = "vivero_asignado_id", length = 36)
    private String viveroAsignadoId;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
