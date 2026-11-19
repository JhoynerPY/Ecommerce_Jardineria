package com.uniquindio.ecommerce.domain.ejemplar;

import java.math.BigDecimal;
import java.util.Objects;

public record Precio(BigDecimal valor) {

    public Precio{
        Objects.requireNonNull(valor, "El precio no puede ser nulo");
        if (valor.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
    }

    public static Precio de(double valor){
        return new Precio(BigDecimal.valueOf(valor));
    }
}
