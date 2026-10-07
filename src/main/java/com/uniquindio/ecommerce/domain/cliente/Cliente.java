package com.uniquindio.ecommerce.domain.cliente;

import java.util.Objects;

public class Cliente {

    private final String id;
    private final String nombre;
    private final Email email;
    private String telefono;
    private String direccion;

    private Cliente(String id, String nombre, Email email, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public static Cliente registrar(String id, String nombre, Email email, String telefono,
                                    String direccion){
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
