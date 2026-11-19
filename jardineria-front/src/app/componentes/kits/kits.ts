import { Component, computed, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { KitService } from '../../servicios/kit.service';
import { AvisoService } from '../../servicios/aviso.service';
import {
  ArticuloKitResponse,
  ArticuloKitSeleccionable,
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

  protected readonly cargando = signal(false);
  protected readonly cargandoArticulos = signal(false);
  protected readonly cargandoKits = signal(false);
  protected readonly mensajeError = signal<string | null>(null);
  protected readonly nombreKit = signal('');
  protected readonly ultimoKit = signal<KitResponse | null>(null);

  protected readonly articulos = signal<ArticuloKitSeleccionable[]>([]);
  protected readonly kits = signal<KitResponse[]>([]);

  protected readonly plantaSeleccionada = signal<ArticuloKitSeleccionable | null>(null);
  protected readonly macetaSeleccionada = signal<ArticuloKitSeleccionable | null>(null);
  protected readonly sustratoSeleccionado = signal<ArticuloKitSeleccionable | null>(null);

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

  protected readonly seleccionados = computed(() =>
    [this.plantaSeleccionada(), this.macetaSeleccionada(), this.sustratoSeleccionado()].filter(
      (articulo): articulo is ArticuloKitSeleccionable => articulo !== null,
    ),
  );

  protected readonly subtotal = computed(() =>
    this.seleccionados().reduce((total, articulo) => total + articulo.precio, 0),
  );
  protected readonly descuento = computed(() => this.subtotal() * 0.15);
  protected readonly total = computed(() => this.subtotal() - this.descuento());
  protected readonly kitCompleto = computed(
    () =>
      this.plantaSeleccionada() !== null &&
      this.macetaSeleccionada() !== null &&
      this.sustratoSeleccionado() !== null,
  );
  protected readonly macetaCompatible = computed(() => {
    const planta = this.plantaSeleccionada();
    const maceta = this.macetaSeleccionada();
    return !planta || !maceta || maceta.diametro >= planta.diametro;
  });

  constructor() {
    this.cargarArticulos();
    this.cargarKits();
  }

  protected actualizarNombre(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.nombreKit.set(input.value);
  }

  protected seleccionar(articulo: ArticuloKitSeleccionable): void {
    if (articulo.tipo === 'ACCESORIO') {
      return;
    }

    switch (articulo.tipo) {
      case 'PLANTA':
        this.plantaSeleccionada.update((actual) =>
          actual?.id === articulo.id ? null : articulo,
        );
        break;
      case 'MACETA':
        this.macetaSeleccionada.update((actual) =>
          actual?.id === articulo.id ? null : articulo,
        );
        break;
      case 'SUSTRATO':
        this.sustratoSeleccionado.update((actual) =>
          actual?.id === articulo.id ? null : articulo,
        );
        break;
    }
  }

  protected esSeleccionado(articulo: ArticuloKitSeleccionable): boolean {
    switch (articulo.tipo) {
      case 'PLANTA':
        return this.plantaSeleccionada()?.id === articulo.id;
      case 'MACETA':
        return this.macetaSeleccionada()?.id === articulo.id;
      case 'SUSTRATO':
        return this.sustratoSeleccionado()?.id === articulo.id;
      default:
        return false;
    }
  }

  protected guardar(): void {
    if (this.cargando()) {
      return;
    }

    if (!this.kitCompleto() || !this.macetaCompatible()) {
      this.mensajeError.set(
        'Selecciona una planta, una maceta compatible y un sustrato antes de guardar.',
      );
      return;
    }

    const planta = this.plantaSeleccionada();
    const maceta = this.macetaSeleccionada();
    const sustrato = this.sustratoSeleccionado();
    if (!planta || !maceta || !sustrato) {
      return;
    }

    const nombreGuardado = this.nombreKit().trim();
    const request: ConfigurarKitRequest = {
      idPlanta: planta.id,
      idMaceta: maceta.id,
      idSustrato: sustrato.id,
      nombreKit: nombreGuardado || undefined,
    };

    this.cargando.set(true);
    this.mensajeError.set(null);
    this.ultimoKit.set(null);

    this.kitService.crearKit(request).subscribe({
      next: (kit) => {
        this.ultimoKit.set(kit);
        this.cargando.set(false);
        this.avisoService.mostrarExito(
          `Kit ${nombreGuardado || kit.idKit} guardado correctamente.`,
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

  private cargarArticulos(): void {
    this.cargandoArticulos.set(true);
    this.kitService.listarArticulos().subscribe({
      next: (articulos) => {
        this.articulos.set(articulos.map((articulo) => this.clasificarArticulo(articulo)));
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

  private limpiarSeleccion(): void {
    this.plantaSeleccionada.set(null);
    this.macetaSeleccionada.set(null);
    this.sustratoSeleccionado.set(null);
    this.nombreKit.set('');
  }

  private clasificarArticulo(articulo: ArticuloKitResponse): ArticuloKitSeleccionable {
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
      nombre.includes('abono')
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
      return 'Uno de los artículos seleccionados ya no existe en el catálogo.';
    }
    if (error.status === 422) {
      return (
        this.extraerMensaje(error) ||
        'La maceta seleccionada no es compatible con la planta o los datos del kit no son válidos.'
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