export interface DetallePedidoRequest {
  idProducto: string;
  cantidad: number;
  precioUnitario: number;
}

export interface CrearPedidoRequest {
  idCliente: string;
  detalles: DetallePedidoRequest[];
  direccion: string;
  tipoExencion: 'EXENCION_TOTAL' | 'ACUERDO_PARCIAL_PERDIDA' | 'SIN_EXENCION';
  terminosExencion: string;
  versionExencion: string;
  exencionAceptada: boolean;
}

export interface PedidoResponse {
  idPedido: string;
  estado: string;
  total: number;
  mensaje: string;
}

export interface CancelarPedidoRequest {
  idPedido: string;
  motivoCancelacion: string;
}