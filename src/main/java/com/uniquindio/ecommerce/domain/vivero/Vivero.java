package com.uniquindio.ecommerce.domain.vivero;

import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Vivero {
    private final String id;
    private final String nombre;
    private final Direccion direccion;
    private final String telefono;
    private final String email;
    private final double radioCoberturaKm;
    private final List<String> especiesDisponiblesIds;
    private boolean activo;

    private Vivero(String id, String nombre, Direccion direccion, String telefono, String email, double radioCoberturaKm) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.radioCoberturaKm = radioCoberturaKm;
        this.especiesDisponiblesIds = new ArrayList<>();
        this.activo = true;
    }

    public static Vivero registrar(String nombre, Direccion direccion, String telefono, String email, double radioCoberturaKm) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaDominioException("El nombre del vivero no puede estar vacío");
        }
        if (direccion == null) {
            throw new ReglaDominioException("La dirección del vivero es obligatoria");
        }
        if (radioCoberturaKm <= 0) {
            throw new ReglaDominioException("El radio de cobertura debe ser mayor a cero");
        }
        return new Vivero(UUID.randomUUID().toString(), nombre, direccion, telefono, email, radioCoberturaKm);
    }

    public boolean cubreDireccion(Direccion direccionCliente) {
        if (!activo) return false;
        return this.direccion.distanciaA(direccionCliente) <= this.radioCoberturaKm;
    }

    public void agregarEspecieDisponible(String especieId) {
        if (especieId != null && !especiesDisponiblesIds.contains(especieId)) {
            this.especiesDisponiblesIds.add(especieId);
        }
    }

    public void removerEspecieDisponible(String especieId) {
        this.especiesDisponiblesIds.remove(especieId);
    }

    public boolean tieneEspecieDisponible(String especieId) {
        return this.especiesDisponiblesIds.contains(especieId);
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public Direccion getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public double getRadioCoberturaKm() { return radioCoberturaKm; }
    public List<String> getEspeciesDisponiblesIds() { return List.copyOf(especiesDisponiblesIds); }
    public boolean isActivo() { return activo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vivero vivero = (Vivero) o;
        return Objects.equals(id, vivero.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}