package com.uniquindio.ecommerce.domain.cliente;

import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import java.util.Objects;

public class Cliente {

    private final String id;
    private final String nombre;
    private final Email email;
    private String telefono;
    private Direccion direccion;
    private String viveroAsignadoId;

    private Cliente(String id, String nombre, Email email, String telefono, Direccion direccion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.viveroAsignadoId = null;
    }

    public static Cliente registrar(String id, String nombre, Email email, String telefono,
                                    Direccion direccion){
        if (direccion == null) {
            throw new ReglaDominioException("La dirección del cliente es obligatoria para asignar vivero cercano");
        }
        return new Cliente(id, nombre, email, telefono, direccion);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Email getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public String getViveroAsignadoId() {
        return viveroAsignadoId;
    }

    public void actualizarDatosContacto(String telefono, Direccion direccion) {
        this.telefono = telefono;
        this.direccion = direccion;
        this.viveroAsignadoId = null;
    }

    public void asignarVivero(String viveroId) {
        if (viveroId != null && !viveroId.trim().isEmpty()) {
            this.viveroAsignadoId = viveroId.trim();
        } else {
            this.viveroAsignadoId = null;
        }
    }

    public boolean tieneViveroAsignado() {
        return viveroAsignadoId != null && !viveroAsignadoId.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
