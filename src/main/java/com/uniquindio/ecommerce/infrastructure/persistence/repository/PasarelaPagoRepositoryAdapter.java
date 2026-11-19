package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.application.ports.output.PasarelaPagoPort;
import org.springframework.stereotype.Repository;

@Repository
public class PasarelaPagoRepositoryAdapter implements PasarelaPagoPort {

    @Override
    public boolean procesarPago(String metodoPago, double montoTotal) {
        // Simulación de pasarela de pago - siempre retorna true para testing
        // En producción se integraría con Stripe, PayPal, etc.
        return true;
    }
}