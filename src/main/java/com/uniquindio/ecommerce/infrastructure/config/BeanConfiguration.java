package com.uniquindio.ecommerce.infrastructure.config;

import com.uniquindio.ecommerce.application.ports.output.*;
import com.uniquindio.ecommerce.application.services.ActualizarEstadoSaludService;
import com.uniquindio.ecommerce.application.services.CancelarPedidoService;
import com.uniquindio.ecommerce.application.services.CrearPedidoService;
import com.uniquindio.ecommerce.application.services.ProcesarCompraService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    //Caso de uso: Cancelar pedido
    @Bean
    public CancelarPedidoService cancelarPedidoService(PedidoRepositoryPort pedidoRepositoryPort, InventarioPort inventarioPort) {
        return new CancelarPedidoService(pedidoRepositoryPort, inventarioPort);
    }

    //Caso de uso: Actualizar estado de salud
    @Bean
    public ActualizarEstadoSaludService actualizarEstadoSaludService(EjemplarRepositoryPort ejemplarRepositoryPort) {
        return new ActualizarEstadoSaludService(ejemplarRepositoryPort);
    }

    //Caso de uso: Crear pedido
    @Bean
    public CrearPedidoService crearPedidoService(ClienteRepositoryPort clienteRepo, ViveroRepositoryPort viveroRepo, PedidoRepositoryPort pedidoRepo) {
        return new CrearPedidoService(clienteRepo, viveroRepo, pedidoRepo);
    }

    //Caso de uso: Procesar compra
    @Bean
    public ProcesarCompraService procesarCompraService(PasarelaPagoPort pagoPort, InventarioPort inventarioPort, PedidoRepositoryPort pedidoRepo) {
        return new ProcesarCompraService(pagoPort, inventarioPort, pedidoRepo);
    }
}