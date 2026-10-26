package com.uniquindio.ecommerce.infrastructure.persistence.mapper;

import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.domain.cliente.Email;
import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.ejemplar.Ejemplar;
import com.uniquindio.ecommerce.domain.ejemplar.EstadoDeSalud;
import com.uniquindio.ecommerce.domain.ejemplar.EtapaCrecimiento;
import com.uniquindio.ecommerce.domain.ejemplar.Precio;
import com.uniquindio.ecommerce.domain.especie.Especie;
import com.uniquindio.ecommerce.domain.especie.FichaDeCuidado;
import com.uniquindio.ecommerce.domain.especie.NivelRiesgo;
import com.uniquindio.ecommerce.domain.especie.RequerimientoLuz;
import com.uniquindio.ecommerce.domain.especie.Riesgo;
import com.uniquindio.ecommerce.domain.kit.ArticuloKit;
import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import com.uniquindio.ecommerce.domain.pedido.ExencionResponsabilidad;
import com.uniquindio.ecommerce.domain.pedido.EstadoPedido;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.domain.vivero.Vivero;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ArticuloKitEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ClienteEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.DetallePedidoEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.DireccionEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.EjemplarEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.EmailEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.EspecieEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ExencionResponsabilidadEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.FichaDeCuidadoEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.KitJardineriaEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PedidoEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PrecioEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.RiesgoEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.TipoExencion;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ViveroEntity;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JpaMapper {

    // Vivero Mappers
    public static ViveroEntity toEntity(Vivero domain) {
        if (domain == null) return null;
        ViveroEntity entity = new ViveroEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDireccion(toDireccionEmbeddable(domain.getDireccion()));
        entity.setTelefono(domain.getTelefono());
        entity.setEmail(domain.getEmail());
        entity.setRadioCoberturaKm(domain.getRadioCoberturaKm());
        entity.setActivo(domain.isActivo());
        return entity;
    }

    public static Vivero toDomain(ViveroEntity entity) {
        if (entity == null) return null;
        try {
            Constructor<Vivero> constructor = Vivero.class.getDeclaredConstructor(
                String.class, String.class, Direccion.class, String.class, String.class, double.class, List.class, boolean.class
            );
            constructor.setAccessible(true);
            return constructor.newInstance(
                entity.getId(),
                entity.getNombre(),
                toDireccionDomain(entity.getDireccion()),
                entity.getTelefono(),
                entity.getEmail(),
                entity.getRadioCoberturaKm(),
                new ArrayList<>(),
                entity.isActivo()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error mapping ViveroEntity to domain", e);
        }
    }

    // Cliente Mappers
    public static ClienteEntity toEntity(Cliente domain) {
        if (domain == null) return null;
        ClienteEntity entity = new ClienteEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setEmail(new EmailEmbeddable(domain.getEmail().valor()));
        entity.setTelefono(domain.getTelefono());
        entity.setDireccion(toDireccionEmbeddable(domain.getDireccion()));
        entity.setViveroAsignadoId(domain.getViveroAsignadoId());
        return entity;
    }

    public static Cliente toDomain(ClienteEntity entity) {
        if (entity == null) return null;
        Cliente cliente = Cliente.registrar(
            entity.getId(),
            entity.getNombre(),
            new Email(entity.getEmail().getValor()),
            entity.getTelefono(),
            toDireccionDomain(entity.getDireccion())
        );
        if (entity.getViveroAsignadoId() != null) {
            cliente.asignarVivero(entity.getViveroAsignadoId());
        }
        return cliente;
    }

    // Especie Mappers
    public static EspecieEntity toEntity(Especie domain) {
        if (domain == null) return null;
        EspecieEntity entity = new EspecieEntity();
        entity.setId(domain.getId());
        entity.setNombreComun(domain.getNombreComun());
        entity.setNombreCientifico(domain.getNombreCientifico());
        entity.setFichaDeCuidado(toFichaDeCuidadoEmbeddable(domain.getFichaDeCuidado()));
        entity.setRiesgo(toRiesgoEmbeddable(domain.getRiesgo()));
        return entity;
    }

    public static Especie toDomain(EspecieEntity entity) {
        if (entity == null) return null;
        return Especie.agregarAlCatalogo(
            entity.getId(),
            entity.getNombreComun(),
            entity.getNombreCientifico(),
            toFichaDeCuidadoDomain(entity.getFichaDeCuidado()),
            toRiesgoDomain(entity.getRiesgo())
        );
    }

    // Ejemplar Mappers
    public static EjemplarEntity toEntity(Ejemplar domain) {
        if (domain == null) return null;
        EjemplarEntity entity = new EjemplarEntity();
        entity.setId(domain.getId());
        entity.setEspecie(toEntity(domain.getEspecie()));
        entity.setEtapaCrecimiento(toEtapaCrecimientoEntity(domain.getEtapaCrecimiento()));
        entity.setPrecio(toPrecioEmbeddable(domain.getPrecio()));
        entity.setDisponibilidad(domain.isDisponibilidad());
        return entity;
    }

    public static Ejemplar toDomain(EjemplarEntity entity) {
        if (entity == null) return null;
        Ejemplar ejemplar = Ejemplar.registrarEnInventario(
            entity.getId(),
            toDomain(entity.getEspecie()),
            toEtapaCrecimientoDomain(entity.getEtapaCrecimiento()),
            toPrecioDomain(entity.getPrecio())
        );
        if (!entity.isDisponibilidad()) {
            ejemplar.retirarDeVenta();
        }
        return ejemplar;
    }

    // ArticuloKit Mappers
    public static ArticuloKitEntity toEntity(ArticuloKit domain) {
        if (domain == null) return null;
        ArticuloKitEntity entity = new ArticuloKitEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setPrecio(toPrecioEmbeddableKit(domain.getPrecio()));
        entity.setDiametro(domain.getDiametro());
        return entity;
    }

    public static ArticuloKit toDomain(ArticuloKitEntity entity) {
        if (entity == null) return null;
        return new ArticuloKit(
            entity.getId(),
            entity.getNombre(),
            toPrecioDomainKit(entity.getPrecio()),
            entity.getDiametro()
        );
    }

    // KitJardineria Mappers
    public static KitJardineriaEntity toEntity(KitJardineria domain) {
        if (domain == null) return null;
        KitJardineriaEntity entity = new KitJardineriaEntity();
        entity.setId(domain.getIdKit());
        entity.setPlanta(toEntity(domain.getPlanta()));
        entity.setMaceta(toEntity(domain.getMaceta()));
        entity.setSustrato(toEntity(domain.getSustrato()));
        return entity;
    }

    public static KitJardineria toDomain(KitJardineriaEntity entity) {
        if (entity == null) return null;
        try {
            Constructor<KitJardineria> constructor = KitJardineria.class.getDeclaredConstructor(
                String.class, ArticuloKit.class, ArticuloKit.class, ArticuloKit.class
            );
            constructor.setAccessible(true);
            return constructor.newInstance(
                entity.getId(),
                toDomain(entity.getPlanta()),
                toDomain(entity.getMaceta()),
                toDomain(entity.getSustrato())
            );
        } catch (Exception e) {
            throw new RuntimeException("Error mapping KitJardineriaEntity to domain", e);
        }
    }

    // Pedido Mappers
    public static PedidoEntity toEntity(Pedido domain) {
        if (domain == null) return null;
        PedidoEntity entity = new PedidoEntity();
        entity.setId(domain.getIdPedido());
        entity.setIdCliente(domain.getIdCliente());
        entity.setIdVivero(domain.getIdVivero());
        entity.setEstado(toEstadoPedidoEntity(domain.getEstado()));
        entity.setDireccionEntrega(domain.getDireccionEntrega());
        entity.setExencionResponsabilidad(toExencionEmbeddable(domain.getExencionResponsabilidad()));

        List<DetallePedidoEntity> detalleEntities = new ArrayList<>();
        for (int i = 0; i < domain.getDetalles().size(); i++) {
            DetallePedido detalle = domain.getDetalles().get(i);
            DetallePedidoEntity detalleEntity = new DetallePedidoEntity();
            detalleEntity.setId(UUID.randomUUID().toString());
            detalleEntity.setPedido(entity);
            detalleEntity.setIdProducto(detalle.getIdProducto());
            detalleEntity.setCantidad(detalle.getCantidad());
            detalleEntity.setPrecioUnitario(detalle.getPrecioUnitario());
            detalleEntities.add(detalleEntity);
        }
        entity.setDetalles(detalleEntities);
        return entity;
    }

    public static Pedido toDomain(PedidoEntity entity) {
        if (entity == null) return null;
        List<DetallePedido> detalles = new ArrayList<>();
        for (DetallePedidoEntity detalleEntity : entity.getDetalles()) {
            detalles.add(new DetallePedido(
                detalleEntity.getIdProducto(),
                detalleEntity.getCantidad(),
                detalleEntity.getPrecioUnitario()
            ));
        }

        Pedido pedido = Pedido.crear(
            entity.getIdCliente(),
            entity.getIdVivero(),
            detalles,
            entity.getDireccionEntrega(),
            toExencionDomain(entity.getExencionResponsabilidad())
        );

        // Override the generated ID with the persisted ID
        try {
            Field idField = Pedido.class.getDeclaredField("idPedido");
            idField.setAccessible(true);
            idField.set(pedido, entity.getId());

            Field estadoField = Pedido.class.getDeclaredField("estado");
            estadoField.setAccessible(true);
            estadoField.set(pedido, toEstadoPedidoDomain(entity.getEstado()));
        } catch (Exception e) {
            // ignore
        }
        return pedido;
    }

    // Helper methods
    private static DireccionEmbeddable toDireccionEmbeddable(Direccion domain) {
        if (domain == null) return null;
        DireccionEmbeddable embeddable = new DireccionEmbeddable();
        embeddable.setCalle(domain.calle());
        embeddable.setCiudad(domain.ciudad());
        embeddable.setDepartamento(domain.departamento());
        embeddable.setCodigoPostal(domain.codigoPostal());
        embeddable.setPais(domain.pais());
        embeddable.setLatitud(domain.latitud());
        embeddable.setLongitud(domain.longitud());
        return embeddable;
    }

    private static Direccion toDireccionDomain(DireccionEmbeddable entity) {
        if (entity == null) return null;
        return Direccion.de(
            entity.getCalle(),
            entity.getCiudad(),
            entity.getDepartamento(),
            entity.getCodigoPostal(),
            entity.getPais(),
            entity.getLatitud(),
            entity.getLongitud()
        );
    }

    private static FichaDeCuidadoEmbeddable toFichaDeCuidadoEmbeddable(FichaDeCuidado domain) {
        if (domain == null) return null;
        FichaDeCuidadoEmbeddable embeddable = new FichaDeCuidadoEmbeddable();
        embeddable.setRequerimientoLuz(toRequerimientoLuzEntity(domain.requerimientoLuz()));
        embeddable.setFrecuenciaRiego(domain.frecuenciaRiego());
        embeddable.setSustrato(domain.sustrato());
        embeddable.setTemporadaFloracion(domain.temporadaFloracion());
        return embeddable;
    }

    private static FichaDeCuidado toFichaDeCuidadoDomain(FichaDeCuidadoEmbeddable entity) {
        if (entity == null) return null;
        return new FichaDeCuidado(
            toRequerimientoLuzDomain(entity.getRequerimientoLuz()),
            entity.getFrecuenciaRiego(),
            entity.getSustrato(),
            entity.getTemporadaFloracion()
        );
    }

    private static RiesgoEmbeddable toRiesgoEmbeddable(Riesgo domain) {
        if (domain == null) return null;
        RiesgoEmbeddable embeddable = new RiesgoEmbeddable();
        embeddable.setNivel(toNivelRiesgoEntity(domain.nivel()));
        embeddable.setDescripcion(domain.descripcion());
        return embeddable;
    }

    private static Riesgo toRiesgoDomain(RiesgoEmbeddable entity) {
        if (entity == null) return null;
        return new Riesgo(toNivelRiesgoDomain(entity.getNivel()), entity.getDescripcion());
    }

    private static ExencionResponsabilidadEmbeddable toExencionEmbeddable(ExencionResponsabilidad domain) {
        if (domain == null) return null;
        ExencionResponsabilidadEmbeddable embeddable = new ExencionResponsabilidadEmbeddable();
        embeddable.setTipo(toTipoExencionEntity(domain.tipo()));
        embeddable.setTerminos(domain.terminos());
        embeddable.setAceptadaPorCliente(domain.aceptadaPorCliente());
        embeddable.setVersion(domain.version());
        return embeddable;
    }

    private static ExencionResponsabilidad toExencionDomain(ExencionResponsabilidadEmbeddable entity) {
        if (entity == null) return null;
        return new ExencionResponsabilidad(
            toTipoExencionDomain(entity.getTipo()),
            entity.getTerminos(),
            entity.isAceptadaPorCliente(),
            entity.getVersion()
        );
    }

    private static PrecioEmbeddable toPrecioEmbeddable(Precio domain) {
        if (domain == null) return null;
        return new PrecioEmbeddable(domain.valor(), "COP");
    }

    private static Precio toPrecioDomain(PrecioEmbeddable entity) {
        if (entity == null) return null;
        return Precio.de(entity.getValor().doubleValue());
    }

    private static PrecioEmbeddable toPrecioEmbeddableKit(com.uniquindio.ecommerce.domain.kit.Precio domain) {
        if (domain == null) return null;
        return new PrecioEmbeddable(BigDecimal.valueOf(domain.getValor()), domain.getMoneda());
    }

    private static com.uniquindio.ecommerce.domain.kit.Precio toPrecioDomainKit(PrecioEmbeddable entity) {
        if (entity == null) return null;
        return new com.uniquindio.ecommerce.domain.kit.Precio(entity.getValor().doubleValue(), entity.getMoneda());
    }

    private static com.uniquindio.ecommerce.infrastructure.persistence.entity.RequerimientoLuz toRequerimientoLuzEntity(RequerimientoLuz domain) {
        if (domain == null) return null;
        return com.uniquindio.ecommerce.infrastructure.persistence.entity.RequerimientoLuz.valueOf(domain.name());
    }

    private static RequerimientoLuz toRequerimientoLuzDomain(com.uniquindio.ecommerce.infrastructure.persistence.entity.RequerimientoLuz entity) {
        if (entity == null) return null;
        return RequerimientoLuz.valueOf(entity.name());
    }

    private static com.uniquindio.ecommerce.infrastructure.persistence.entity.NivelRiesgo toNivelRiesgoEntity(NivelRiesgo domain) {
        if (domain == null) return null;
        return com.uniquindio.ecommerce.infrastructure.persistence.entity.NivelRiesgo.valueOf(domain.name());
    }

    private static NivelRiesgo toNivelRiesgoDomain(com.uniquindio.ecommerce.infrastructure.persistence.entity.NivelRiesgo entity) {
        if (entity == null) return null;
        return NivelRiesgo.valueOf(entity.name());
    }

    private static TipoExencion toTipoExencionEntity(ExencionResponsabilidad.TipoExencion domain) {
        if (domain == null) return null;
        return TipoExencion.valueOf(domain.name());
    }

    private static ExencionResponsabilidad.TipoExencion toTipoExencionDomain(TipoExencion entity) {
        if (entity == null) return null;
        return ExencionResponsabilidad.TipoExencion.valueOf(entity.name());
    }

    private static com.uniquindio.ecommerce.infrastructure.persistence.entity.EstadoPedido toEstadoPedidoEntity(EstadoPedido domain) {
        if (domain == null) return null;
        return com.uniquindio.ecommerce.infrastructure.persistence.entity.EstadoPedido.valueOf(domain.name());
    }

    private static EstadoPedido toEstadoPedidoDomain(com.uniquindio.ecommerce.infrastructure.persistence.entity.EstadoPedido entity) {
        if (entity == null) return null;
        return EstadoPedido.valueOf(entity.name());
    }

    private static com.uniquindio.ecommerce.infrastructure.persistence.entity.EtapaCrecimiento toEtapaCrecimientoEntity(EtapaCrecimiento domain) {
        if (domain == null) return null;
        return com.uniquindio.ecommerce.infrastructure.persistence.entity.EtapaCrecimiento.valueOf(domain.name());
    }

    private static EtapaCrecimiento toEtapaCrecimientoDomain(com.uniquindio.ecommerce.infrastructure.persistence.entity.EtapaCrecimiento entity) {
        if (entity == null) return null;
        return EtapaCrecimiento.valueOf(entity.name());
    }
}