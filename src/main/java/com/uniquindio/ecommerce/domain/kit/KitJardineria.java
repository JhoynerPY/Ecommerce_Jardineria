package com.uniquindio.ecommerce.domain.kit;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class KitJardineria {
    private final String idKit;
    private final List<ArticuloKitSeleccionado> articulos;
    private final String nombreKit;
    private final String idCliente;
    private static final double DESCUENTO_PAQUETE = 0.15;

    private KitJardineria(String idKit, List<ArticuloKitSeleccionado> articulos,
                          String nombreKit, String idCliente) {
        this.idKit = idKit;
        this.articulos = articulos;
        this.nombreKit = nombreKit;
        this.idCliente = idCliente;
    }

    public static KitJardineria crear(List<ArticuloKitSeleccionado> articulos, String nombreKit) {
        return crear(articulos, nombreKit, null);
    }

    public static KitJardineria crear(List<ArticuloKitSeleccionado> articulos, String nombreKit,
                                      String idCliente) {
        if (articulos == null || articulos.isEmpty()) {
            throw new ReglaDominioException("El KitJardineria debe contener al menos un ArticuloKit.");
        }

        Set<String> identificadores = new HashSet<>();
        for (ArticuloKitSeleccionado seleccionado : articulos) {
            if (seleccionado == null || seleccionado.articulo() == null) {
                throw new ReglaDominioException("Todos los ArticuloKit del kit son obligatorios.");
            }
            if (!identificadores.add(seleccionado.articulo().getId())) {
                throw new ReglaDominioException("No se puede repetir el mismo ArticuloKit en un KitJardineria.");
            }
        }

        return new KitJardineria(UUID.randomUUID().toString(), List.copyOf(articulos), nombreKit, idCliente);
    }

    public static KitJardineria crear(ArticuloKit planta, ArticuloKit maceta, ArticuloKit sustrato) {
        return crear(planta, maceta, sustrato, null);
    }

    public static KitJardineria crear(ArticuloKit planta, ArticuloKit maceta,
                                      ArticuloKit sustrato, String nombreKit) {
        if (planta == null || maceta == null || sustrato == null) {
            throw new ReglaDominioException("El KitJardineria debe contener una planta, una maceta y un sustrato.");
        }

        if (maceta.getDiametro() < planta.getDiametro()) {
            throw new ReglaDominioException("La maceta seleccionada no es compatible con las dimensiones de la planta.");
        }

        return crear(List.of(
                new ArticuloKitSeleccionado(planta, 1),
                new ArticuloKitSeleccionado(maceta, 1),
                new ArticuloKitSeleccionado(sustrato, 1)
        ), nombreKit);
    }

    public static KitJardineria restaurar(String idKit, List<ArticuloKitSeleccionado> articulos,
                                          String nombreKit, String idCliente) {
        return new KitJardineria(idKit, List.copyOf(articulos), nombreKit, idCliente);
    }

    public double calcularSubtotal() {
        return articulos.stream()
                .mapToDouble(seleccionado -> seleccionado.articulo().getPrecio().getValor() * seleccionado.cantidad())
                .sum();
    }

    public double calcularDescuento() {
        return calcularSubtotal() * DESCUENTO_PAQUETE;
    }

    public double calcularPrecioTotal() {
        return calcularSubtotal() - calcularDescuento();
    }

    public String getIdKit() { return idKit; }
    public List<ArticuloKitSeleccionado> getArticulos() { return articulos; }
    public String getNombreKit() { return nombreKit; }
    public String getIdCliente() { return idCliente; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KitJardineria that = (KitJardineria) o;
        return Objects.equals(idKit, that.idKit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idKit);
    }
}