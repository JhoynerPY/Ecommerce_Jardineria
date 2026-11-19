export type TipoExencion =
  | 'EXENCION_TOTAL'
  | 'ACUERDO_PARCIAL_PERDIDA'
  | 'SIN_EXENCION';

export interface DetallePedidoRequest {
  idProducto: string;
  cantidad: number;
  precioUnitario: number;
}

export interface CrearPedidoRequest {
  idCliente?: string | null;
  detalles: DetallePedidoRequest[];
  direccion: string;
  tipoExencion: TipoExencion;
  porcentajeExencion?: number | null;
  terminosExencion: string;
  versionExencion: string;
  exencionAceptada: boolean;
}

export interface CrearPedidoResponse {
  idPedido: string;
  estado: string;
  total: number;
  mensaje: string;
  idVivero: string | null;
  nombreVivero: string | null;
}

export interface CancelarPedidoRequest {
  idPedido: string;
  motivoCancelacion: string;
}

export interface CancelarPedidoResponse {
  idPedido: string;
  estado: string;
  mensaje: string;
}

export interface ProcesarCompraRequest {
  idPedido: string;
  metodoPago: string;
}

export interface ProcesarCompraResponse {
  idPedido: string;
  estado: string;
  mensaje: string;
}
