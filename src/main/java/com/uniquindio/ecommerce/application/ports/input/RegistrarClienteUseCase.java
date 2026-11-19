package com.uniquindio.ecommerce.application.ports.input;

import com.uniquindio.ecommerce.application.dto.RegistrarClienteCommand;
import com.uniquindio.ecommerce.domain.cliente.Cliente;

public interface RegistrarClienteUseCase {
    Cliente registrar(RegistrarClienteCommand command);
}
