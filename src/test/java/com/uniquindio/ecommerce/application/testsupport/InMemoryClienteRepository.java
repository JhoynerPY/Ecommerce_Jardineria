package com.uniquindio.ecommerce.application.testsupport;

import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryClienteRepository implements ClienteRepositoryPort {

    private final Map<String, Cliente> clientes = new ConcurrentHashMap<>();
    private final Map<String, String> emailToId = new ConcurrentHashMap<>();

    @Override
    public Cliente guardar(Cliente cliente) {
        clientes.put(cliente.getId(), cliente);
        emailToId.put(cliente.getEmail().valor(), cliente.getId());
        return cliente;
    }

    @Override
    public Optional<Cliente> buscarPorId(String id) {
        return Optional.ofNullable(clientes.get(id));
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        String id = emailToId.get(email);
        return id != null ? Optional.ofNullable(clientes.get(id)) : Optional.empty();
    }

    @Override
    public boolean existePorEmail(String email) {
        return emailToId.containsKey(email);
    }
}