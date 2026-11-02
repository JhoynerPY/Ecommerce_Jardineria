package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.*;

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
    @AttributeOverrides({
            @AttributeOverride(name = "tipo", column = @Column(name = "exencion_responsabilidad_tipo")),
            @AttributeOverride(name = "terminos", column = @Column(name = "exencion_responsabilidad_terminos")),
            @AttributeOverride(name = "aceptadaPorCliente", column = @Column(name = "exencion_responsabilidad_aceptada_por_cliente")),
            @AttributeOverride(name = "version", column = @Column(name = "exencion_responsabilidad_version"))
    })
    private ExencionResponsabilidadEmbeddable exencionResponsabilidad;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
