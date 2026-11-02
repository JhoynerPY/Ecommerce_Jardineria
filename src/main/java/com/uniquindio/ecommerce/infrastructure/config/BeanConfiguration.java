package com.uniquindio.ecommerce.infrastructure.config;

import com.uniquindio.ecommerce.application.ports.output.EjemplarRepositoryPort;
import com.uniquindio.ecommerce.application.services.ActualizarEstadoSaludService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ActualizarEstadoSaludService actualizarEstadoSaludService (EjemplarRepositoryPort ejemplarRepositoryPort){
        return new ActualizarEstadoSaludService(ejemplarRepositoryPort);
    }
}
