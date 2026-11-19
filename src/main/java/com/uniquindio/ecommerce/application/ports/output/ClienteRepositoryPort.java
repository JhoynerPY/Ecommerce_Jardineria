package com.uniquindio.ecommerce.application.ports.output;

import com.uniquindio.ecommerce.domain.cliente.Cliente;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(String id);
    Optional<Cliente> buscarPorEmail(String email);
    boolean existePorEmail(String email);
}
