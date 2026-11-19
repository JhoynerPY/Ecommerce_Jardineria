export interface PerfilResponse {
  nombre: string;
  email: string;
  rol: string;
  direccion: string;
  totalPedidos: number;
  totalKits: number;
}

export interface DetallePedidoResponse {
  idProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface PedidoResumenResponse {
  id: string;
  fechaCreacion: string;
  estado: string;
  viveroAsignado: string;
  direccionEntrega: string;
  tipoExencion: string;
  porcentajeAcuerdo: number | null;
  detalles: DetallePedidoResponse[];
  total: number;
}