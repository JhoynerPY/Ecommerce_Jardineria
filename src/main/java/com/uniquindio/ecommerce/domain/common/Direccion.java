package com.uniquindio.ecommerce.domain.common;

import java.util.Objects;
import java.util.regex.Pattern;

public record Direccion(String calle, String ciudad, String departamento, String codigoPostal, String pais, double latitud, double longitud) {

    private static final Pattern CODIGO_POSTAL_PATTERN = Pattern.compile("^\\d{5,6}$");

    public Direccion {
        Objects.requireNonNull(calle, "La calle no puede ser nula");
        Objects.requireNonNull(ciudad, "La ciudad no puede ser nula");
        Objects.requireNonNull(departamento, "El departamento no puede ser nulo");
        Objects.requireNonNull(codigoPostal, "El código postal no puede ser nulo");
        Objects.requireNonNull(pais, "El país no puede ser nulo");

        if (calle.trim().isEmpty()) throw new IllegalArgumentException("La calle no puede estar vacía");
        if (ciudad.trim().isEmpty()) throw new IllegalArgumentException("La ciudad no puede estar vacía");
        if (departamento.trim().isEmpty()) throw new IllegalArgumentException("El departamento no puede estar vacío");
        if (!CODIGO_POSTAL_PATTERN.matcher(codigoPostal).matches()) throw new IllegalArgumentException("Código postal inválido");
        if (pais.trim().isEmpty()) throw new IllegalArgumentException("El país no puede estar vacío");
        if (latitud < -90 || latitud > 90) throw new IllegalArgumentException("Latitud fuera de rango válido (-90 a 90)");
        if (longitud < -180 || longitud > 180) throw new IllegalArgumentException("Longitud fuera de rango válido (-180 a 180)");
    }

    public static Direccion de(String calle, String ciudad, String departamento, String codigoPostal, String pais, double latitud, double longitud) {
        return new Direccion(calle, ciudad, departamento, codigoPostal, pais, latitud, longitud);
    }

    public double distanciaA(Direccion otra) {
        final int R = 6371;
        double lat1 = Math.toRadians(this.latitud);
        double lat2 = Math.toRadians(otra.latitud);
        double deltaLat = Math.toRadians(otra.latitud - this.latitud);
        double deltaLon = Math.toRadians(otra.longitud - this.longitud);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s %s, %s", calle, ciudad, departamento, codigoPostal, pais);
    }
}