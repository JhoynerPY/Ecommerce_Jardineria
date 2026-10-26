package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "kits_jardineria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE kits_jardineria SET deleted = true WHERE id = ?")

public class KitJardineriaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "planta_id", nullable = false)
    private ArticuloKitEntity planta;

    @ManyToOne
    @JoinColumn(name = "maceta_id", nullable = false)
    private ArticuloKitEntity maceta;

    @ManyToOne
    @JoinColumn(name = "sustrato_id", nullable = false)
    private ArticuloKitEntity sustrato;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
