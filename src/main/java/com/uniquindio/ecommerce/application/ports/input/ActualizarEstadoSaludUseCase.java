package com.uniquindio.ecommerce.application.ports.input;

import com.uniquindio.ecommerce.application.dto.ActualizarEstadoSaludCommand;
import com.uniquindio.ecommerce.domain.ejemplar.Ejemplar;

public interface ActualizarEstadoSaludUseCase {
    Ejemplar actualizar(ActualizarEstadoSaludCommand command);
}
