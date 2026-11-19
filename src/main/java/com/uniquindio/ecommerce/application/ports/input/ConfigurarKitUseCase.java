package com.uniquindio.ecommerce.application.ports.input;

import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import java.util.List;

public interface ConfigurarKitUseCase {
    KitJardineria armarKit(String idPlanta, String idMaceta, String idSustrato, String idCliente) throws Exception;
    List<KitJardineria> listarKits();
}