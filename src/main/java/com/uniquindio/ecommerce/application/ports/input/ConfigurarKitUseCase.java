package com.uniquindio.ecommerce.application.ports.input;

import com.uniquindio.ecommerce.application.dto.ArticuloKitSeleccionadoCommand;
import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import java.util.List;

public interface ConfigurarKitUseCase {
    KitJardineria armarKit(List<ArticuloKitSeleccionadoCommand> articulos,
                           String idCliente, String nombreKit) throws Exception;
    List<KitJardineria> listarKits();
    List<KitJardineria> listarKitsPorCliente(String idCliente);
}