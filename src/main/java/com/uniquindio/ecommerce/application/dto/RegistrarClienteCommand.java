package com.uniquindio.ecommerce.application.dto;

import com.uniquindio.ecommerce.domain.common.Direccion;

public class RegistrarClienteCommand {
    private final String id;
    private final String nombre;
    private final String email;
    private final String telefono;
    private final Direccion direccion;

    public RegistrarClienteCommand(String id, String nombre, String email, String telefono, Direccion direccion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public Direccion getDireccion() { return direccion; }
}
