export type EstadoDeSalud =
  | 'SANO'
  | 'EN_RECUPERACION'
  | 'CON_PLAGA'
  | 'MARCHITO';

export interface ActualizarEstadoSaludRequest {
  idEjemplar: string;
  nuevoEstado: EstadoDeSalud;
}

export interface ActualizarEstadoSaludResponse {
  idEjemplar: string;
  estadoSalud: string;
  disponibleParaVenta: boolean;
  mensaje: string;
}

export interface OpcionEstadoSalud {
  valor: EstadoDeSalud;
  etiqueta: string;
  descripcion: string;
}
