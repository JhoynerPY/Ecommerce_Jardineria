package com.uniquindio.ecommerce.domain.kit;

import java.util.Objects;

public class Precio {
    private final double valor;
    private final String moneda;

    public Precio(double valor, String moneda) {
        if (valor < 0) throw new ReglaDominioException("El precio no puede ser negativo");
        this.valor = valor;
        this.moneda = moneda;
    }

    // VO: Igualdad por VALOR
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Precio precio = (Precio) o;
        return Double.compare(precio.valor, valor) == 0 && Objects.equals(moneda, precio.moneda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor, moneda);
    }
    public double getValor() {
        return valor;
    }
}