package com.uniquindio.ecommerce.domain.ejemplar;

public enum EtapaCrecimiento {
    PLANTULA,
    JOVEN,
    ADULTA,
    FLORACION;

    public boolean puedeTransicionarA(EtapaCrecimiento siguiente){
        return switch (this){
            case PLANTULA -> siguiente == JOVEN;
            case JOVEN -> siguiente == ADULTA;
            case ADULTA -> siguiente == FLORACION;
            case FLORACION -> false;
        };
    }

    public boolean esFinal(){
        return this == FLORACION;
    }
}
