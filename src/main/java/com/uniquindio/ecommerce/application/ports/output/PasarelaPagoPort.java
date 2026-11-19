package com.uniquindio.ecommerce.application.ports.output;

public interface PasarelaPagoPort {
    boolean procesarPago(String metodoPago, double montoTotal);
}