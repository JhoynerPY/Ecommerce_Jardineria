package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE pedidos SET deleted = true WHERE id = ?")

public class PedidoEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "id_cliente", length = 36, nullable = false)
    private String idCliente;

    @Column(name = "id_vivero", length = 36, nullable = false)
    private String idVivero;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedidoEntity> detalles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE_PAGO;

    @Column(name = "direccion_entrega", nullable = false, length = 500)
    private String direccionEntrega;

    @Embedded
    private ExencionResponsabilidadEmbeddable exencionResponsabilidad;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
