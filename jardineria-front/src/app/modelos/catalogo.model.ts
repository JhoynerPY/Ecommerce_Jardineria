export type EtapaCrecimiento = 'PLANTULA' | 'JOVEN' | 'ADULTA' | 'FLORACION';

export interface PlantaBusquedaDTO {
  ejemplarId: string;
  especieNombreComun: string;
  especieNombreCientifico: string;
  etapaCrecimiento: EtapaCrecimiento | string | null;
  precio: number | null;
  moneda: string | null;
  disponible: boolean | null;
  viveroNombre: string | null;
  viveroId: string | null;
}

export interface FiltrosCatalogo {
  nombre?: string;
  precioMin?: number;
  precioMax?: number;
  etapa?: EtapaCrecimiento;
}

export interface PaginaResponse<T> {
  contenido: T[];
  paginaActual: number;
  totalElementos: number;
  totalPaginas: number;
}
