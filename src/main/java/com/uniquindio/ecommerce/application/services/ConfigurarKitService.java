package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.ports.input.ConfigurarKitUseCase;
import com.uniquindio.ecommerce.application.ports.output.CarritoComprasPort;
import com.uniquindio.ecommerce.application.ports.output.CatalogoArticulosPort;
import com.uniquindio.ecommerce.domain.kit.ArticuloKit;
import com.uniquindio.ecommerce.domain.kit.KitJardineria;

public class ConfigurarKitService implements ConfigurarKitUseCase {

    private final CatalogoArticulosPort catalogoArticulosPort;
    private final CarritoComprasPort carritoComprasPort;

    public ConfigurarKitService(CatalogoArticulosPort catalogoArticulosPort, CarritoComprasPort carritoComprasPort) {
        this.catalogoArticulosPort = catalogoArticulosPort;
        this.carritoComprasPort = carritoComprasPort;
    }

    @Override
    public KitJardineria armarKit(String idPlanta, String idMaceta, String idSustrato, String idCliente) throws Exception {

        // 1. Obtener la información base de los artículos seleccionados
        ArticuloKit planta = catalogoArticulosPort.obtenerArticulo(idPlanta);
        ArticuloKit maceta = catalogoArticulosPort.obtenerArticulo(idMaceta);
        ArticuloKit sustrato = catalogoArticulosPort.obtenerArticulo(idSustrato);

        if (planta == null || maceta == null || sustrato == null) {
            throw new Exception("Uno o más artículos no existen en el catálogo.");
        }

        // 2. Crear el Kit (El Dominio se encarga de validar si la maceta sirve para la planta y de aplicar el descuento)
        KitJardineria nuevoKit = new KitJardineria(planta, maceta, sustrato);

        // 3. Añadir el combo completo al carrito del usuario
        carritoComprasPort.agregarKit(idCliente, nuevoKit);

        return nuevoKit;
    }
}