package com.uniquindio.ecommerce.infrastructure.config;

import com.uniquindio.ecommerce.application.ports.output.InventarioPort;
import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.application.services.CancelarPedidoService;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.InventarioRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CancelarPedidoService cancelarPedidoService(PedidoRepositoryPort pedidoRepositoryPort,
                                                       InventarioPort inventarioPort){
        return new CancelarPedidoService(pedidoRepositoryPort, inventarioPort);
    }
}
