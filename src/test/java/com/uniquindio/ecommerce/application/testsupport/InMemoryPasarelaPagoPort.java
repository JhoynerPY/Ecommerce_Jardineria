package com.uniquindio.ecommerce.application.testsupport;

import com.uniquindio.ecommerce.application.ports.output.PasarelaPagoPort;

public class InMemoryPasarelaPagoPort implements PasarelaPagoPort {

    private boolean debeFallar = false;

    @Override
    public boolean procesarPago(String metodoPago, double montoTotal) {
        return !debeFallar;
    }

    public void setDebeFallar(boolean debeFallar) {
        this.debeFallar = debeFallar;
    }
}