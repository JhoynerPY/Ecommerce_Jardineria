export interface ViveroAliado {
  nombre: string;
  distanciaKm: number;
  ciudad: string;
}

export interface MetricaHome {
  icono: string;
  etiqueta: string;
  valor: string;
  color: 'bosque' | 'terracota';
}

export interface BloqueValor {
  icono: string;
  titulo: string;
  descripcion: string;
  sello: string;
  color: 'bosque' | 'terracota';
}

export interface EjemplarDestacado {
  nombre: string;
  nombreCientifico: string;
  precio: number;
  vivero: ViveroAliado;
  etapa: string;
  toxicidad: 'seguro' | 'moderado' | 'peligroso';
  luz: string;
  riego: string;
}
