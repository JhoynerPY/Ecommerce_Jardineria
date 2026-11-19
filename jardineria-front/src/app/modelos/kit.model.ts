export interface ArticuloKitResponse {
  id: string;
  nombre: string;
  precio: number;
  moneda: string;
  diametro: number;
  cantidad: number;
}

export interface KitResponse {
  idKit: string;
  nombreKit: string | null;
  idCliente: string | null;
  articulos: ArticuloKitResponse[];
  subtotal: number;
  descuento: number;
  precioTotal: number;
}

export interface ConfigurarKitRequest {
  articulos: Array<{ articuloId: string; cantidad: number }>;
  nombreKit?: string;
}

export type TipoAplicacion = 'PLANTA' | 'MACETA' | 'SUSTRATO' | 'ACCESORIO';

export interface ArticuloKitSeleccionable extends ArticuloKitResponse {
  tipo: TipoAplicacion;
}

export interface ArticuloSeleccionado {
  articulo: ArticuloKitSeleccionable;
  cantidad: number;
}