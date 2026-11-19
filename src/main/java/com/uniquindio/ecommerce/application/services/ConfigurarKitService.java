package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.ports.input.ConfigurarKitUseCase;
import com.uniquindio.ecommerce.application.ports.output.CarritoComprasPort;
import com.uniquindio.ecommerce.application.ports.output.CatalogoArticulosPort;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.kit.ArticuloKit;
import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import com.uniquindio.ecommerce.domain.repository.KitJardineriaRepository;

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
    public KitJardineria armarKit(String idPlanta, String idMaceta, String idSustrato, String idCliente) {

        ArticuloKit planta = catalogoArticulosPort.obtenerArticulo(idPlanta);
        ArticuloKit maceta = catalogoArticulosPort.obtenerArticulo(idMaceta);
        ArticuloKit sustrato = catalogoArticulosPort.obtenerArticulo(idSustrato);

        if (planta == null || maceta == null || sustrato == null) {
            throw new ReglaDominioException("Uno o más artículos no existen en el catálogo.");
        }

        KitJardineria nuevoKit = KitJardineria.crear(planta, maceta, sustrato);

        kitJardineriaRepository.almacenarKit(nuevoKit);
        carritoComprasPort.agregarKit(idCliente, nuevoKit);

        return nuevoKit;
    }

    @Override
    public List<KitJardineria> listarKits() {
        return kitJardineriaRepository.obtenerKits();
    }
}