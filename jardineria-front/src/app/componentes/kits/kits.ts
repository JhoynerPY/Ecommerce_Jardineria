import { Component, computed, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { KitService } from '../../servicios/kit.service';
import { AvisoService } from '../../servicios/aviso.service';
import { CarritoService } from '../../servicios/carrito.service';
import {
  ArticuloKitResponse,
  ArticuloKitSeleccionable,
  ArticuloSeleccionado,
  ConfigurarKitRequest,
  KitResponse,
  TipoAplicacion,
} from '../../modelos/kit.model';

@Component({
  standalone: true,
  imports: [DecimalPipe],
  selector: 'app-kits',
  templateUrl: './kits.html',
  styleUrl: './kits.css',
})
export class Kits {
  private readonly kitService = inject(KitService);
  private readonly avisoService = inject(AvisoService);
  protected readonly carritoService = inject(CarritoService);

  protected readonly pestana = signal<'listos' | 'personalizar'>('listos');

  protected readonly cargando = signal(false);
  protected readonly cargandoArticulos = signal(false);
  protected readonly cargandoKits = signal(false);
  protected readonly mensajeError = signal<string | null>(null);
  protected readonly nombreKit = signal('');
  protected readonly ultimoKit = signal<KitResponse | null>(null);

  protected readonly articulos = signal<ArticuloKitSeleccionable[]>([]);
  protected readonly kits = signal<KitResponse[]>([]);
  protected readonly seleccion = signal<Record<string, number>>({});

  protected readonly kitsDisponibles = computed(() =>
    this.kits().filter((kit) => !kit.idCliente),
  );

  protected readonly plantas = computed(() =>
    this.articulos().filter((articulo) => articulo.tipo === 'PLANTA'),
  );
  protected readonly macetas = computed(() =>
    this.articulos().filter((articulo) => articulo.tipo === 'MACETA'),
  );
  protected readonly sustratos = computed(() =>
    this.articulos().filter((articulo) => articulo.tipo === 'SUSTRATO'),
  );
  protected readonly accesorios = computed(() =>
    this.articulos().filter((articulo) => articulo.tipo === 'ACCESORIO'),
  );

  protected readonly seleccionados = computed<ArticuloSeleccionado[]>(() => {
    const seleccion = this.seleccion();
    return this.articulos()
      .filter((articulo) => seleccion[articulo.id] !== undefined)
      .map((articulo) => ({ articulo, cantidad: seleccion[articulo.id] }));
  });

  protected readonly subtotal = computed(() =>
    this.seleccionados().reduce(
      (total, seleccionado) =>
        total + seleccionado.articulo.precio * seleccionado.cantidad,
      0,
    ),
  );
  protected readonly descuento = computed(() => this.subtotal() * 0.15);
  protected readonly total = computed(() => this.subtotal() - this.descuento());
  protected readonly kitValido = computed(() => this.seleccionados().length > 0);

  constructor() {
    this.cargarArticulos();
    this.cargarKits();
  }

  protected cambiarPestana(pestana: 'listos' | 'personalizar'): void {
    this.pestana.set(pestana);
  }

  protected actualizarNombre(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.nombreKit.set(input.value);
  }

  protected alternar(articulo: ArticuloKitSeleccionable): void {
    const nuevaSeleccion = { ...this.seleccion() };
    if (nuevaSeleccion[articulo.id] !== undefined) {
      delete nuevaSeleccion[articulo.id];
    } else {
      nuevaSeleccion[articulo.id] = 1;
    }
    this.seleccion.set(nuevaSeleccion);
  }

  protected aumentar(articuloId: string): void {
    this.actualizarCantidad(articuloId, this.cantidadSeleccionada(articuloId) + 1);
  }

  protected disminuir(articuloId: string): void {
    const cantidad = this.cantidadSeleccionada(articuloId);
    if (cantidad <= 1) {
      this.quitar(articuloId);
      return;
    }
    this.actualizarCantidad(articuloId, cantidad - 1);
  }

  protected quitar(articuloId: string): void {
    const nuevaSeleccion = { ...this.seleccion() };
    delete nuevaSeleccion[articuloId];
    this.seleccion.set(nuevaSeleccion);
  }

  protected cantidadSeleccionada(articuloId: string): number {
    return this.seleccion()[articuloId] ?? 0;
  }

  protected estaSeleccionado(articuloId: string): boolean {
    return this.seleccion()[articuloId] !== undefined;
  }

  protected agregarKitAlCarrito(kit: KitResponse): void {
    this.carritoService.agregarKit({
      kitId: kit.idKit,
      nombre: kit.nombreKit || 'KitJardineria sin nombre',
      cantidadArticulos: kit.articulos.length,
      precioConDescuento: kit.precioTotal,
    });
    this.avisoService.mostrarExito(
      `KitJardineria "${kit.nombreKit || kit.idKit}" agregado al carrito.`,
    );
  }

  protected guardar(): void {
    if (this.cargando()) {
      return;
    }

    if (!this.kitValido()) {
      this.mensajeError.set('Selecciona al menos un ArticuloKit antes de guardar.');
      return;
    }

    const nombreGuardado = this.nombreKit().trim();
    const request: ConfigurarKitRequest = {
      articulos: this.seleccionados().map((seleccionado) => ({
        articuloId: seleccionado.articulo.id,
        cantidad: seleccionado.cantidad,
      })),
      nombreKit: nombreGuardado || undefined,
    };

    this.cargando.set(true);
    this.mensajeError.set(null);
    this.ultimoKit.set(null);

    this.kitService.crearKit(request).subscribe({
      next: (kit) => {
        this.ultimoKit.set(kit);
        this.carritoService.agregarKit({
          kitId: kit.idKit,
          nombre: kit.nombreKit || 'KitJardineria sin nombre',
          cantidadArticulos: kit.articulos.length,
          precioConDescuento: kit.precioTotal,
        });
        this.cargando.set(false);
        this.avisoService.mostrarExito(
          `Kit ${nombreGuardado || kit.idKit} guardado y añadido al carrito.`,
        );
        this.limpiarSeleccion();
        this.cargarKits();
      },
      error: (error: HttpErrorResponse) => {
        const mensaje = this.traducirError(error);
        this.mensajeError.set(mensaje);
        this.cargando.set(false);
        this.avisoService.mostrarError(mensaje);
      },
    });
  }

  protected cargarKits(): void {
    this.cargandoKits.set(true);
    this.kitService.listarKits().subscribe({
      next: (kits) => {
        this.kits.set(kits);
        this.cargandoKits.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargandoKits.set(false);
        const mensaje = this.traducirError(error);
        this.avisoService.mostrarError(mensaje);
      },
    });
  }

  private actualizarCantidad(articuloId: string, cantidad: number): void {
    this.seleccion.set({ ...this.seleccion(), [articuloId]: cantidad });
  }

  private limpiarSeleccion(): void {
    this.seleccion.set({});
    this.nombreKit.set('');
  }

  private cargarArticulos(): void {
    this.cargandoArticulos.set(true);
    this.kitService.listarArticulos().subscribe({
      next: (articulos) => {
        this.articulos.set(
          articulos.map((articulo) => this.clasificarArticulo(articulo)),
        );
        this.cargandoArticulos.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargandoArticulos.set(false);
        const mensaje = this.traducirError(error);
        this.mensajeError.set(mensaje);
        this.avisoService.mostrarError(mensaje);
      },
    });
  }

  private clasificarArticulo(
    articulo: ArticuloKitResponse,
  ): ArticuloKitSeleccionable {
    const id = articulo.id.toLowerCase();
    const nombre = articulo.nombre.toLowerCase();

    let tipo: TipoAplicacion = 'ACCESORIO';
    if (
      id.startsWith('planta-') ||
      nombre.includes('planta') ||
      nombre.includes('rosa') ||
      nombre.includes('orqu') ||
      nombre.includes('suculenta') ||
      nombre.includes('cactus') ||
      nombre.includes('helecho')
    ) {
      tipo = 'PLANTA';
    } else if (
      id.startsWith('maceta-') ||
      nombre.includes('maceta') ||
      nombre.includes('matera')
    ) {
      tipo = 'MACETA';
    } else if (
      id.startsWith('sustrato-') ||
      nombre.includes('sustrato') ||
      nombre.includes('compost') ||
      nombre.includes('tierra') ||
      nombre.includes('abono') ||
      nombre.includes('perlita')
    ) {
      tipo = 'SUSTRATO';
    }

    return { ...articulo, tipo };
  }

  private traducirError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. Verifica tu conexión e intenta nuevamente.';
    }
    if (error.status === 400) {
      return this.extraerMensaje(error) || 'Revisa los datos del kit e intenta nuevamente.';
    }
    if (error.status === 401) {
      return 'Tu sesión venció o no es válida. Inicia sesión nuevamente.';
    }
    if (error.status === 403) {
      return 'No tienes permisos para configurar kits.';
    }
    if (error.status === 404) {
      return 'Uno de los ArticuloKit seleccionados ya no existe en el catálogo.';
    }
    if (error.status === 422) {
      return (
        this.extraerMensaje(error) ||
        'Los datos del KitJardineria no son válidos.'
      );
    }
    return 'Ocurrió un error inesperado al guardar el kit.';
  }

  private extraerMensaje(error: HttpErrorResponse): string | null {
    const cuerpo = error.error;
    if (typeof cuerpo === 'string') {
      return cuerpo;
    }
    if (cuerpo && typeof cuerpo === 'object') {
      if (typeof cuerpo.message === 'string') {
        return cuerpo.message;
      }
      const mensajes = Object.values(cuerpo).filter(
        (valor): valor is string => typeof valor === 'string',
      );
      if (mensajes.length > 0) {
        return mensajes.join(' ');
      }
    }
    return null;
  }
}