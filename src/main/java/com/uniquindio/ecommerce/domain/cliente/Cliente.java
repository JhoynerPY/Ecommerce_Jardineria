package com.uniquindio.ecommerce.domain.cliente;

import java.util.Objects;

public class Cliente {

    private Long id;
    private String nombre;
    private Email email;
    private String telefono;
    private String direccion;

    public Cliente(Long id, String nombre, Email email, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Long getId() {
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

    public String getDireccion() {
        return direccion;
    }

    public void actualizarDatosContacto (String telefono, String direccion){
        this.telefono = telefono;
        this.direccion = direccion;
    }

    //Comparando cada cliente por su unica id
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id) ;
    }

    //Comparando cada cliente por su unica id
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
