package com.uniquindio.ecommerce.application.ports.input;

import com.uniquindio.ecommerce.domain.kit.KitJardineria;

public interface ConfigurarKitUseCase {
    KitJardineria armarKit(String idPlanta, String idMaceta, String idSustrato, String idCliente) throws Exception;
}