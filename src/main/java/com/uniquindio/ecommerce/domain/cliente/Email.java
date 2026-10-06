package com.uniquindio.ecommerce.domain.cliente;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String valor) {

    private static final Pattern FORMATO_VALIDO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public Email{
        Objects.requireNonNull(valor, "El email no puede ser nulo");
        if (!FORMATO_VALIDO.matcher(valor).matches()){
            throw new IllegalArgumentException("Formato o caracter de email invalido: "+ valor);
        }
    }
}
