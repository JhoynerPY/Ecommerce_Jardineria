package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.dto.ActualizarEstadoSaludCommand;
import com.uniquindio.ecommerce.application.ports.input.ActualizarEstadoSaludUseCase;
import com.uniquindio.ecommerce.application.ports.output.EjemplarRepositoryPort;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.ejemplar.Ejemplar;
import com.uniquindio.ecommerce.domain.ejemplar.EstadoDeSalud;

public class ActualizarEstadoSaludService implements ActualizarEstadoSaludUseCase {

    private final EjemplarRepositoryPort ejemplarRepositoryPort;

    public ActualizarEstadoSaludService(EjemplarRepositoryPort ejemplarRepositoryPort) {
        this.ejemplarRepositoryPort = ejemplarRepositoryPort;
    }

    @Override
    public Ejemplar actualizar(ActualizarEstadoSaludCommand command) {
        Ejemplar ejemplar = ejemplarRepositoryPort.buscarPorId(command.getIdEjemplar()).
                orElseThrow(() -> new ReglaDominioException("Ejemplar no encontrado: " + command.getIdEjemplar()));

        try {
            EstadoDeSalud nuevoEstado = EstadoDeSalud.valueOf(command.getNuevoEstado().toUpperCase());
            ejemplar.actualizarEstadoSalud(nuevoEstado);

        } catch (IllegalArgumentException e){
            throw new ReglaDominioException("Estado de salud Invalido: " + command.getNuevoEstado());
        }

        return ejemplarRepositoryPort.guardar(ejemplar);
    }
}
