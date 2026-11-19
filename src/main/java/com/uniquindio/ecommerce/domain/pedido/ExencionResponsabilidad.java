package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import java.util.Objects;

public record ExencionResponsabilidad(TipoExencion tipo, String terminos, boolean aceptadaPorCliente, String version) {

    public enum TipoExencion {
        EXENCION_TOTAL,
        ACUERDO_PARCIAL_PERDIDA,
        SIN_EXENCION
    }

    public ExencionResponsabilidad {
        Objects.requireNonNull(tipo, "El tipo de exención no puede ser nulo");
        Objects.requireNonNull(terminos, "Los términos no pueden ser nulos");
        Objects.requireNonNull(version, "La versión no puede ser nula");

        if (terminos.trim().isEmpty()) throw new ReglaDominioException("Los términos de exención no pueden estar vacíos");
        if (version.trim().isEmpty()) throw new ReglaDominioException("La versión de los términos no puede estar vacía");

        if (tipo != TipoExencion.SIN_EXENCION && !aceptadaPorCliente) {
            throw new ReglaDominioException("El cliente debe aceptar los términos de exención de responsabilidad");
        }
    }

    public static ExencionResponsabilidad exencionTotal(String terminos, String version, boolean aceptadaPorCliente) {
        return new ExencionResponsabilidad(TipoExencion.EXENCION_TOTAL, terminos, aceptadaPorCliente, version);
    }

    public static ExencionResponsabilidad acuerdoParcialPerdida(String terminos, String version, boolean aceptadaPorCliente) {
        return new ExencionResponsabilidad(TipoExencion.ACUERDO_PARCIAL_PERDIDA, terminos, aceptadaPorCliente, version);
    }

    public static ExencionResponsabilidad sinExencion() {
        return new ExencionResponsabilidad(TipoExencion.SIN_EXENCION, "Sin exención de responsabilidad aplicada", true, "1.0");
    }

    public boolean esExencionTotal() {
        return tipo == TipoExencion.EXENCION_TOTAL;
    }

    public boolean esAcuerdoParcial() {
        return tipo == TipoExencion.ACUERDO_PARCIAL_PERDIDA;
    }

    public boolean requiereAceptacion() {
        return tipo != TipoExencion.SIN_EXENCION;
    }

    public double calcularCobertura(double valorTotal) {
        return switch (tipo) {
            case EXENCION_TOTAL -> 0.0;
            case ACUERDO_PARCIAL_PERDIDA -> valorTotal * 0.5;
            case SIN_EXENCION -> valorTotal;
        };
    }
}