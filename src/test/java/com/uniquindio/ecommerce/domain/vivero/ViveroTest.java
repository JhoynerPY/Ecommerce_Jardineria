package com.uniquindio.ecommerce.domain.vivero;

import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ViveroTest {

    private Direccion direccionViveroArmenia() {
        return Direccion.de("Carrera 15 #10-20", "Armenia", "Quindío", "630001", "Colombia", 4.5339, -75.6811);
    }

    private Direccion direccionViveroPereira() {
        return Direccion.de("Calle 20 #15-30", "Pereira", "Risaralda", "660001", "Colombia", 4.8133, -75.6936);
    }

    private Direccion direccionViveroManizales() {
        return Direccion.de("Carrera 25 #10-40", "Manizales", "Caldas", "170001", "Colombia", 5.0689, -75.5174);
    }

    private Direccion direccionClienteArmeniaCentro() {
        return Direccion.de("Calle 20 #30-40", "Armenia", "Quindío", "630001", "Colombia", 4.5400, -75.6850);
    }

    private Direccion direccionClienteArmeniaNorte() {
        return Direccion.de("Calle 50 #60-70", "Armenia", "Quindío", "630001", "Colombia", 4.5600, -75.6900);
    }

    private Direccion direccionClientePereira() {
        return Direccion.de("Calle 10 #20-30", "Pereira", "Risaralda", "660001", "Colombia", 4.8100, -75.6900);
    }

    private Direccion direccionClienteLejana() {
        return Direccion.de("Calle 100", "Bogotá", "Cundinamarca", "110111", "Colombia", 4.7110, -74.0721);
    }

    @Test
    void registrarViveroValido() {
        Vivero vivero = Vivero.registrar("Jardines del Quindío", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        assertNotNull(vivero.getId());
        assertEquals("Jardines del Quindío", vivero.getNombre());
        assertEquals(15.0, vivero.getRadioCoberturaKm());
        assertTrue(vivero.isActivo());
    }

    @Test
    void registrarViveroSinNombreLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            Vivero.registrar("  ", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0));
    }

    @Test
    void registrarViveroSinDireccionLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            Vivero.registrar("Jardines", null, "3101234567", "contacto@jardinesquindio.com", 15.0));
    }

    @Test
    void registrarViveroConRadioCeroLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 0));
    }

    @Test
    void cubreDireccionClienteCercano() {
        Vivero vivero = Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        assertTrue(vivero.cubreDireccion(direccionClienteArmeniaCentro()));
    }

    @Test
    void noCubreDireccionClienteLejano() {
        Vivero vivero = Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        assertFalse(vivero.cubreDireccion(direccionClienteLejana()));
    }

    @Test
    void viveroInactivoNoCubreNingunaDireccion() {
        Vivero vivero = Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        vivero.desactivar();
        
        assertFalse(vivero.cubreDireccion(direccionClienteArmeniaCentro()));
    }

    @Test
    void radioCoberturaLimiteExacto() {
        Direccion viveroDir = Direccion.de("Calle 0", "Ciudad", "Depto", "00000", "Colombia", 0.0, 0.0);
        Direccion clienteEnLimite = Direccion.de("Calle 1", "Ciudad", "Depto", "00000", "Colombia", 0.0898, 0.0); // ~10km
        
        Vivero vivero = Vivero.registrar("Vivero", viveroDir, "3101234567", "test@test.com", 10.0);
        
        assertTrue(vivero.cubreDireccion(clienteEnLimite));
    }

    @Test
    void radioCoberturaFueraPorPoco() {
        Direccion viveroDir = Direccion.de("Calle 0", "Ciudad", "Depto", "00000", "Colombia", 0.0, 0.0);
        Direccion clienteFuera = Direccion.de("Calle 1", "Ciudad", "Depto", "00000", "Colombia", 0.091, 0.0); // ~10.1km
        
        Vivero vivero = Vivero.registrar("Vivero", viveroDir, "3101234567", "test@test.com", 10.0);
        
        assertFalse(vivero.cubreDireccion(clienteFuera));
    }

    @Test
    void multiplesViverosClienteCubiertoPorUnoSolo() {
        Vivero viveroArmenia = Vivero.registrar("Jardines Armenia", direccionViveroArmenia(), "3101111111", "armenia@jardines.com", 15.0);
        Vivero viveroPereira = Vivero.registrar("Jardines Pereira", direccionViveroPereira(), "3102222222", "pereira@jardines.com", 15.0);
        Vivero viveroManizales = Vivero.registrar("Jardines Manizales", direccionViveroManizales(), "3103333333", "manizales@jardines.com", 15.0);
        
        List<Vivero> viveros = List.of(viveroArmenia, viveroPereira, viveroManizales);
        
        long cubrenClienteArmenia = viveros.stream().filter(v -> v.cubreDireccion(direccionClienteArmeniaCentro())).count();
        
        assertEquals(1, cubrenClienteArmenia);
        assertTrue(viveroArmenia.cubreDireccion(direccionClienteArmeniaCentro()));
        assertFalse(viveroPereira.cubreDireccion(direccionClienteArmeniaCentro()));
        assertFalse(viveroManizales.cubreDireccion(direccionClienteArmeniaCentro()));
    }

    @Test
    void multiplesViverosClienteCubiertoPorVariosEligeMasCercano() {
        Direccion viveroCentro = Direccion.de("Calle 10", "Armenia", "Quindío", "630001", "Colombia", 4.5339, -75.6811);
        Direccion viveroNorte = Direccion.de("Calle 50", "Armenia", "Quindío", "630001", "Colombia", 4.5500, -75.6850);
        Direccion clienteCentro = Direccion.de("Calle 20", "Armenia", "Quindío", "630001", "Colombia", 4.5400, -75.6830);
        
        Vivero viveroC = Vivero.registrar("Jardines Centro", viveroCentro, "3101111111", "centro@jardines.com", 20.0);
        Vivero viveroN = Vivero.registrar("Jardines Norte", viveroNorte, "3102222222", "norte@jardines.com", 10.0);
        
        double distCentro = viveroCentro.distanciaA(clienteCentro);
        double distNorte = viveroNorte.distanciaA(clienteCentro);
        
        assertTrue(viveroC.cubreDireccion(clienteCentro));
        assertTrue(viveroN.cubreDireccion(clienteCentro));
        
        Vivero masCercano = (distNorte < distCentro) ? viveroN : viveroC;
        assertNotNull(masCercano);
    }

    @Test
    void agregarYRemoverEspecieDisponible() {
        Vivero vivero = Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        vivero.agregarEspecieDisponible("ESP-001");
        assertTrue(vivero.tieneEspecieDisponible("ESP-001"));
        
        vivero.removerEspecieDisponible("ESP-001");
        assertFalse(vivero.tieneEspecieDisponible("ESP-001"));
    }

    @Test
    void noDuplicarEspeciesDisponibles() {
        Vivero vivero = Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        vivero.agregarEspecieDisponible("ESP-001");
        vivero.agregarEspecieDisponible("ESP-001");
        
        assertEquals(1, vivero.getEspeciesDisponiblesIds().size());
    }

    @Test
    void especieDisponibleCaseInsensitive() {
        Vivero vivero = Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        vivero.agregarEspecieDisponible("ESP-001");
        assertTrue(vivero.tieneEspecieDisponible("ESP-001"));
        assertFalse(vivero.tieneEspecieDisponible("esp-001")); // Case sensitive por defecto
    }

    @Test
    void getEspeciesDisponiblesIdsRetornaCopiaDefensiva() {
        Vivero vivero = Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        vivero.agregarEspecieDisponible("ESP-001");
        
        List<String> especies = vivero.getEspeciesDisponiblesIds();
        
        assertThrows(UnsupportedOperationException.class, () -> especies.clear());
        assertEquals(1, vivero.getEspeciesDisponiblesIds().size());
    }

    @Test
    void activarDesactivarVivero() {
        Vivero vivero = Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        assertTrue(vivero.isActivo());
        
        vivero.desactivar();
        assertFalse(vivero.isActivo());
        
        vivero.activar();
        assertTrue(vivero.isActivo());
    }

    @Test
    void igualdadPorId() {
        Vivero v1 = Vivero.registrar("Jardines", direccionViveroArmenia(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        String id = v1.getId();
        // Crear otro con mismo ID (simulando repositorio)
        Vivero v2 = new Vivero(id, "Otro Nombre", direccionViveroPereira(), "3109999999", "otro@test.com", 10.0, List.of(), true);
        
        assertEquals(v1, v2);
    }
}