package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.ports.input.RegistrarClienteUseCase;
import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.application.ports.output.ViveroRepositoryPort;
import com.uniquindio.ecommerce.application.dto.RegistrarClienteCommand;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.vivero.Vivero;
import java.util.Optional;

public class RegistrarClienteService implements RegistrarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ViveroRepositoryPort viveroRepositoryPort;

    public RegistrarClienteService(ClienteRepositoryPort clienteRepositoryPort,
                                   ViveroRepositoryPort viveroRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.viveroRepositoryPort = viveroRepositoryPort;
    }

    @Override
    public Cliente registrar(RegistrarClienteCommand command) {
        if (clienteRepositoryPort.existePorEmail(command.getEmail())) {
            throw new ReglaDominioException("Ya existe un cliente registrado con el email: " + command.getEmail());
        }

        Direccion direccion = command.getDireccion();
        Optional<Vivero> viveroMasCercano = viveroRepositoryPort.buscarViveroMasCercano(
            direccion.latitud(), direccion.longitud());

        if (viveroMasCercano.isEmpty()) {
            throw new ReglaDominioException("No hay viveros disponibles que cubran su direccion");
        }

        Vivero vivero = viveroMasCercano.get();
        if (!vivero.cubreDireccion(direccion)) {
            throw new ReglaDominioException("El vivero mas cercano no cubre su direccion (fuera de radio de cobertura)");
        }

        Cliente cliente = Cliente.registrar(
            command.getId(),
            command.getNombre(),
            new com.uniquindio.ecommerce.domain.cliente.Email(command.getEmail()),
            command.getTelefono(),
            direccion
        );

        cliente.asignarVivero(vivero.getId());
        return clienteRepositoryPort.guardar(cliente);
    }
}