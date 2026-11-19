import { Injectable, signal } from '@angular/core';

export type AvisoTipo = 'success' | 'danger' | 'warning' | 'info';

export interface Aviso {
  id: number;
  tipo: AvisoTipo;
  mensaje: string;
}

@Injectable({ providedIn: 'root' })
export class AvisoService {
  private readonly avisos = signal<Aviso[]>([]);
  private contador = 0;

  readonly avisos$ = this.avisos;

  mostrarExito(mensaje: string): void {
    this.agregar('success', mensaje);
  }

  mostrarError(mensaje: string): void {
    this.agregar('danger', mensaje);
  }

  mostrarInfo(mensaje: string): void {
    this.agregar('info', mensaje);
  }

  cerrar(id: number): void {
    this.avisos.set(this.avisos().filter((a) => a.id !== id));
  }

  private agregar(tipo: AvisoTipo, mensaje: string): void {
    const id = ++this.contador;
    const aviso: Aviso = { id, tipo, mensaje };
    this.avisos.set([...this.avisos(), aviso]);

    setTimeout(() => {
      this.cerrar(id);
    }, 4000);
  }
}
