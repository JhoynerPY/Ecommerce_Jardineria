export interface ArticuloKitResponse {
  id: string;
  nombre: string;
  precio: number;
  moneda: string;
  diametro: number;
}

export interface KitResponse {
  idKit: string;
  nombreKit: string | null;
  planta: ArticuloKitResponse;
  maceta: ArticuloKitResponse;
  sustrato: ArticuloKitResponse;
  subtotal: number;
  descuento: number;
  precioTotal: number;
}

export interface ConfigurarKitRequest {
  idPlanta: string;
  idMaceta: string;
  idSustrato: string;
  nombreKit?: string;
}

export type TipoAplicacion = 'PLANTA' | 'MACETA' | 'SUSTRATO' | 'ACCESORIO';

export interface ArticuloKitSeleccionable extends ArticuloKitResponse {
  tipo: TipoAplicacion;
}