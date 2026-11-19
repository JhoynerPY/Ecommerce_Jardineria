package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.dto.ArticuloKitSeleccionadoCommand;
import com.uniquindio.ecommerce.application.ports.input.ConfigurarKitUseCase;
import com.uniquindio.ecommerce.application.ports.output.CarritoComprasPort;
import com.uniquindio.ecommerce.application.ports.output.CatalogoArticulosPort;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.kit.ArticuloKit;
import com.uniquindio.ecommerce.domain.kit.ArticuloKitSeleccionado;
import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import com.uniquindio.ecommerce.domain.repository.KitJardineriaRepository;

import java.util.ArrayList;
import java.util.List;

public class ConfigurarKitService implements ConfigurarKitUseCase {

    private final CatalogoArticulosPort catalogoArticulosPort;
    private final CarritoComprasPort carritoComprasPort;
    private final KitJardineriaRepository kitJardineriaRepository;

    public ConfigurarKitService(CatalogoArticulosPort catalogoArticulosPort,
                                CarritoComprasPort carritoComprasPort,
                                KitJardineriaRepository kitJardineriaRepository) {
        this.catalogoArticulosPort = catalogoArticulosPort;
        this.carritoComprasPort = carritoComprasPort;
        this.kitJardineriaRepository = kitJardineriaRepository;
    }

    @Override
    public KitJardineria armarKit(List<ArticuloKitSeleccionadoCommand> articulos,
                                  String idCliente, String nombreKit) {
        if (articulos == null || articulos.isEmpty()) {
            throw new ReglaDominioException("El KitJardineria debe contener al menos un ArticuloKit.");
        }

        List<ArticuloKitSeleccionado> seleccionados = new ArrayList<>();
        for (ArticuloKitSeleccionadoCommand comando : articulos) {
            ArticuloKit articulo = catalogoArticulosPort.obtenerArticulo(comando.getArticuloId());
            if (articulo == null) {
                throw new ReglaDominioException("Uno o más ArticuloKit no existen en el catálogo.");
            }
            seleccionados.add(new ArticuloKitSeleccionado(articulo, comando.getCantidad()));
        }

        KitJardineria nuevoKit = KitJardineria.crear(seleccionados, nombreKit, idCliente);

        kitJardineriaRepository.almacenarKit(nuevoKit);
        carritoComprasPort.agregarKit(idCliente, nuevoKit);

        return nuevoKit;
    }

    @Override
    public List<KitJardineria> listarKits() {
        return kitJardineriaRepository.obtenerKits();
    }

    @Override
    public List<KitJardineria> listarKitsPorCliente(String idCliente) {
        return kitJardineriaRepository.buscarPorClienteId(idCliente);
    }
}