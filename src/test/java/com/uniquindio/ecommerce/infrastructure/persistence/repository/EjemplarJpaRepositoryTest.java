package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EjemplarEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.EspecieEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ViveroEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.DireccionEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.FichaDeCuidadoEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.RiesgoEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PrecioEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.RequerimientoLuz;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.NivelRiesgo;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.EtapaCrecimiento;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.EjemplarJpaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.EspecieJpaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.ViveroJpaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.PlantaBusquedaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({EjemplarJpaRepository.class, EspecieJpaRepository.class, ViveroJpaRepository.class})
class EjemplarJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EjemplarJpaRepository ejemplarRepository;

    @Autowired
    private EspecieJpaRepository especieRepository;

    @Autowired
    private ViveroJpaRepository viveroRepository;

    private EspecieEntity crearEspecie(String id, String nombreComun, String nombreCientifico) {
        EspecieEntity especie = new EspecieEntity();
        especie.setId(id);
        especie.setNombreComun(nombreComun);
        especie.setNombreCientifico(nombreCientifico);
        especie.setFichaDeCuidado(new FichaDeCuidadoEmbeddable(
                RequerimientoLuz.SOL, "Cada 2 días", "Tierra negra", "Primavera"
        ));
        especie.setRiesgo(new RiesgoEmbeddable(NivelRiesgo.NINGUNO, "Sin riesgo"));
        return entityManager.persistAndFlush(especie);
    }

    private EjemplarEntity crearEjemplar(String id, EspecieEntity especie, EtapaCrecimiento etapa, double precio, boolean disponible) {
        EjemplarEntity ejemplar = new EjemplarEntity();
        ejemplar.setId(id);
        ejemplar.setEspecie(especie);
        ejemplar.setEtapaCrecimiento(etapa);
        ejemplar.setPrecio(new PrecioEmbeddable(BigDecimal.valueOf(precio), "COP"));
        ejemplar.setDisponibilidad(disponible);
        return entityManager.persistAndFlush(ejemplar);
    }

    @Test
    void findByIdAndDeletedFalse_debeRetornarEjemplarExistente() {
        // Given
        EspecieEntity especie = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        EjemplarEntity ejemplar = crearEjemplar("ej-001", especie, EtapaCrecimiento.ADULTA, 25000, true);

        // When
        Optional<EjemplarEntity> resultado = ejemplarRepository.findByIdAndDeletedFalse("ej-001");

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo("ej-001");
        assertThat(resultado.get().getEspecie().getNombreComun()).isEqualTo("Rosa");
        assertThat(resultado.get().isDisponibilidad()).isTrue();
    }

    @Test
    void findByIdAndDeletedFalse_debeRetornarEmptySiEjemplarEliminado() {
        // Given
        EspecieEntity especie = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        EjemplarEntity ejemplar = crearEjemplar("ej-001", especie, EtapaCrecimiento.ADULTA, 25000, true);
        ejemplar.setDeleted(true);
        entityManager.persistAndFlush(ejemplar);

        // When
        Optional<EjemplarEntity> resultado = ejemplarRepository.findByIdAndDeletedFalse("ej-001");

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    void findByDisponibilidadTrueAndDeletedFalse_debeRetornarSoloDisponibles() {
        // Given
        EspecieEntity especie = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        crearEjemplar("ej-001", especie, EtapaCrecimiento.ADULTA, 25000, true);
        crearEjemplar("ej-002", especie, EtapaCrecimiento.JOVEN, 15000, true);
        EjemplarEntity noDisponible = crearEjemplar("ej-003", especie, EtapaCrecimiento.ADULTA, 25000, false);

        // When
        List<EjemplarEntity> resultado = ejemplarRepository.findByDisponibilidadTrueAndDeletedFalse();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(EjemplarEntity::getId).containsExactlyInAnyOrder("ej-001", "ej-002");
    }

    @Test
    void findWithFilters_debeFiltrarPorNombreEspecie() {
        // Given
        EspecieEntity rosa = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        EspecieEntity orquidea = crearEspecie("esp-002", "Orquídea", "Phalaenopsis");
        crearEjemplar("ej-001", rosa, EtapaCrecimiento.ADULTA, 25000, true);
        crearEjemplar("ej-002", orquidea, EtapaCrecimiento.JOVEN, 35000, true);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlantaBusquedaDTO> resultado = ejemplarRepository.buscarPlantasPorNombreYPrecio(
                "Rosa", null, null, null, null, pageable
        );

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getEspecieNombreComun()).isEqualTo("Rosa");
    }

    @Test
    void findWithFilters_debeFiltrarPorRangoPrecio() {
        // Given
        EspecieEntity especie = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        crearEjemplar("ej-001", especie, EtapaCrecimiento.ADULTA, 15000, true);
        crearEjemplar("ej-002", especie, EtapaCrecimiento.JOVEN, 25000, true);
        crearEjemplar("ej-003", especie, EtapaCrecimiento.FLORACION, 35000, true);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlantaBusquedaDTO> resultado = ejemplarRepository.buscarPlantasPorNombreYPrecio(
                null, 20000.0, 30000.0, null, null, pageable
        );

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getPrecio()).isEqualTo(BigDecimal.valueOf(25000));
    }

    @Test
    void findWithFilters_debeFiltrarPorDisponibilidad() {
        // Given
        EspecieEntity especie = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        crearEjemplar("ej-001", especie, EtapaCrecimiento.ADULTA, 25000, true);
        crearEjemplar("ej-002", especie, EtapaCrecimiento.JOVEN, 15000, false);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlantaBusquedaDTO> resultado = ejemplarRepository.buscarPlantasPorNombreYPrecio(
                null, null, null, true, null, pageable
        );

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getEjemplarId()).isEqualTo("ej-001");
    }

    @Test
    void findWithFilters_debeFiltrarPorEtapaCrecimiento() {
        // Given
        EspecieEntity especie = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        crearEjemplar("ej-001", especie, EtapaCrecimiento.ADULTA, 25000, true);
        crearEjemplar("ej-002", especie, EtapaCrecimiento.JOVEN, 15000, true);
        crearEjemplar("ej-003", especie, EtapaCrecimiento.FLORACION, 35000, true);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlantaBusquedaDTO> resultado = ejemplarRepository.buscarPlantasPorNombreYPrecio(
                null, null, null, null, EtapaCrecimiento.FLORACION, pageable
        );

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getEtapaCrecimiento()).isEqualTo("FLORACION");
    }

    @Test
    void contarDisponiblesPorEspecie_debeRetornarConteoCorrecto() {
        // Given
        EspecieEntity rosa = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        EspecieEntity orquidea = crearEspecie("esp-002", "Orquídea", "Phalaenopsis");
        crearEjemplar("ej-001", rosa, EtapaCrecimiento.ADULTA, 25000, true);
        crearEjemplar("ej-002", rosa, EtapaCrecimiento.JOVEN, 15000, true);
        crearEjemplar("ej-003", rosa, EtapaCrecimiento.FLORACION, 35000, false);
        crearEjemplar("ej-004", orquidea, EtapaCrecimiento.ADULTA, 45000, true);

        // When
        List<Object[]> resultado = ejemplarRepository.contarDisponiblesPorEspecie();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado).anyMatch(row -> row[0].equals("Rosa") && ((Number) row[1]).intValue() == 2);
        assertThat(resultado).anyMatch(row -> row[0].equals("Orquídea") && ((Number) row[1]).intValue() == 1);
    }

    @Test
    void estadisticasPrecioPorEspecie_debeRetornarPromedioMinMax() {
        // Given
        EspecieEntity rosa = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        crearEjemplar("ej-001", rosa, EtapaCrecimiento.ADULTA, 20000, true);
        crearEjemplar("ej-002", rosa, EtapaCrecimiento.JOVEN, 30000, true);
        crearEjemplar("ej-003", rosa, EtapaCrecimiento.FLORACION, 40000, true);

        // When
        List<Object[]> resultado = ejemplarRepository.estadisticasPrecioPorEspecie();

        // Then
        assertThat(resultado).hasSize(1);
        Object[] row = resultado.get(0);
        assertThat(row[0]).isEqualTo("Rosa");
        assertThat(((Number) row[1]).doubleValue()).isEqualTo(30000.0); // promedio
        assertThat(((Number) row[2]).doubleValue()).isEqualTo(20000.0); // min
        assertThat(((Number) row[3]).doubleValue()).isEqualTo(40000.0); // max
    }

    @Test
    void softDelete_debeMarcarComoEliminadoSinBorrarFisicamente() {
        // Given
        EspecieEntity especie = crearEspecie("esp-001", "Rosa", "Rosa rubiginosa");
        EjemplarEntity ejemplar = crearEjemplar("ej-001", especie, EtapaCrecimiento.ADULTA, 25000, true);

        // When
        ejemplarRepository.delete(ejemplar);
        entityManager.flush();

        // Then
        Optional<EjemplarEntity> eliminado = ejemplarRepository.findById("ej-001");
        assertThat(eliminado).isPresent();
        assertThat(eliminado.get().isDeleted()).isTrue();

        // No debe aparecer en consultas normales
        Optional<EjemplarEntity> noEliminado = ejemplarRepository.findByIdAndDeletedFalse("ej-001");
        assertThat(noEliminado).isEmpty();
    }
}