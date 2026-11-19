import { Injectable, computed, signal } from '@angular/core';

export interface CarritoEjemplar {
  ejemplarId: string;
  nombreEspecie: string;
  etapaCrecimiento: string;
  precio: number;
  cantidad: number;
  nivelRiesgo: string;
}

export interface CarritoKit {
  kitId: string;
  nombre: string;
  cantidadArticulos: number;
  precioConDescuento: number;
  cantidad: number;
}

@Injectable({ providedIn: 'root' })
export class CarritoService {
  private readonly claveStorage = 'carrito-raiz-local';

  readonly ejemplares = signal<CarritoEjemplar[]>([]);
  readonly kits = signal<CarritoKit[]>([]);

  readonly totalUnidades = computed(
    () =>
      this.ejemplares().reduce((total, ejemplar) => total + ejemplar.cantidad, 0) +
      this.kits().reduce((total, kit) => total + kit.cantidad, 0),
  );

  readonly subtotalEjemplares = computed(() =>
    this.ejemplares().reduce(
      (total, ejemplar) => total + ejemplar.precio * ejemplar.cantidad,
      0,
    ),
  );

  readonly subtotalKits = computed(() =>
    this.kits().reduce(
      (total, kit) => total + kit.precioConDescuento * kit.cantidad,
      0,
    ),
  );

  readonly totalGeneral = computed(
    () => this.subtotalEjemplares() + this.subtotalKits(),
  );

  constructor() {
    this.cargarDesdeAlmacenamiento();
  }

  agregarEjemplar(ejemplar: Omit<CarritoEjemplar, 'cantidad'> & { cantidad?: number }): void {
    const cantidad = ejemplar.cantidad ?? 1;
    const actuales = this.ejemplares();
    const existente = actuales.find((item) => item.ejemplarId === ejemplar.ejemplarId);

    if (existente) {
      this.ejemplares.set(
        actuales.map((item) =>
          item.ejemplarId === ejemplar.ejemplarId
            ? { ...item, cantidad: item.cantidad + cantidad }
            : item,
        ),
      );
    } else {
      this.ejemplares.set([...actuales, { ...ejemplar, cantidad }]);
    }

    this.persistir();
  }

  agregarKit(kit: Omit<CarritoKit, 'cantidad'> & { cantidad?: number }): void {
    const cantidad = kit.cantidad ?? 1;
    const actuales = this.kits();
    const existente = actuales.find((item) => item.kitId === kit.kitId);

    if (existente) {
      this.kits.set(
        actuales.map((item) =>
          item.kitId === kit.kitId
            ? { ...item, cantidad: item.cantidad + cantidad }
            : item,
        ),
      );
    } else {
      this.kits.set([...actuales, { ...kit, cantidad }]);
    }

    this.persistir();
  }

  actualizarCantidad(tipo: 'ejemplar' | 'kit', id: string, cantidad: number): void {
    if (cantidad <= 0) {
      this.eliminar(tipo, id);
      return;
    }

    if (tipo === 'ejemplar') {
      this.ejemplares.set(
        this.ejemplares().map((item) =>
          item.ejemplarId === id ? { ...item, cantidad } : item,
        ),
      );
    } else {
      this.kits.set(
        this.kits().map((item) =>
          item.kitId === id ? { ...item, cantidad } : item,
        ),
      );
    }

    this.persistir();
  }

  eliminar(tipo: 'ejemplar' | 'kit', id: string): void {
    if (tipo === 'ejemplar') {
      this.ejemplares.set(
        this.ejemplares().filter((item) => item.ejemplarId !== id),
      );
    } else {
      this.kits.set(this.kits().filter((item) => item.kitId !== id));
    }
    this.persistir();
  }

  vaciar(): void {
    this.ejemplares.set([]);
    this.kits.set([]);
    this.persistir();
  }

  private cargarDesdeAlmacenamiento(): void {
    try {
      const almacenado = localStorage.getItem(this.claveStorage);
      if (!almacenado) {
        return;
      }

      const datos = JSON.parse(almacenado) as {
        ejemplares?: CarritoEjemplar[];
        kits?: CarritoKit[];
      };
      this.ejemplares.set(Array.isArray(datos.ejemplares) ? datos.ejemplares : []);
      this.kits.set(Array.isArray(datos.kits) ? datos.kits : []);
    } catch {
      this.ejemplares.set([]);
      this.kits.set([]);
    }
  }

  private persistir(): void {
    try {
      localStorage.setItem(
        this.claveStorage,
        JSON.stringify({
          ejemplares: this.ejemplares(),
          kits: this.kits(),
        }),
      );
    } catch {
    }
  }
}