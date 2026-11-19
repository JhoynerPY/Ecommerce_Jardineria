package com.uniquindio.ecommerce.application.dto;

import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import com.uniquindio.ecommerce.domain.pedido.ExencionResponsabilidad;
import java.util.List;

public class CrearPedidoCommand {
    private final String idCliente;
    private final List<DetallePedido> detalles;
    private final Direccion direccionEntrega;
    private final ExencionResponsabilidad.TipoExencion tipoExencion;
    private final String terminosExencion;
    private final String versionExencion;
    private final boolean exencionAceptada;

    public CrearPedidoCommand(String idCliente, List<DetallePedido> detalles, Direccion direccionEntrega,
                              ExencionResponsabilidad.TipoExencion tipoExencion, String terminosExencion,
                              String versionExencion, boolean exencionAceptada) {
        this.idCliente = idCliente;
        this.detalles = detalles;
        this.direccionEntrega = direccionEntrega;
        this.tipoExencion = tipoExencion;
        this.terminosExencion = terminosExencion;
        this.versionExencion = versionExencion;
        this.exencionAceptada = exencionAceptada;
    }

    public String getIdCliente() { return idCliente; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public Direccion getDireccionEntrega() { return direccionEntrega; }
    public ExencionResponsabilidad.TipoExencion getTipoExencion() { return tipoExencion; }
    public String getTerminosExencion() { return terminosExencion; }
    public String getVersionExencion() { return versionExencion; }
    public boolean isExencionAceptada() { return exencionAceptada; }
}