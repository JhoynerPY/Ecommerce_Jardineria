import { Component, computed, effect, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { CatalogoService } from '../../servicios/catalogo.service';
import { AvisoService } from '../../servicios/aviso.service';
import {
  EtapaCrecimiento,
  FiltrosCatalogo,
  PaginaResponse,
  PlantaBusquedaDTO,
} from '../../modelos/catalogo.model';

@Component({
  selector: 'app-catalogo',
  imports: [DecimalPipe],
  styleUrl: './catalogo.css',
  templateUrl: './catalogo.html',
})
export class Catalogo {
  private readonly catalogoService = inject(CatalogoService);
  private readonly avisoService = inject(AvisoService);
  private readonly router = inject(Router);

  protected readonly etapas: { valor: EtapaCrecimiento; etiqueta: string }[] = [
    { valor: 'PLANTULA', etiqueta: 'Plántula en desarrollo' },
    { valor: 'JOVEN', etiqueta: 'Joven' },
    { valor: 'ADULTA', etiqueta: 'Adulta climatizada' },
    { valor: 'FLORACION', etiqueta: 'Floración activa' },
  ];

  protected readonly nombre = signal('');
  protected readonly etapa = signal<EtapaCrecimiento | ''>('');
  protected readonly precioMin = signal<number | null>(null);
  protected readonly precioMax = signal<number | null>(null);

  protected readonly pagina = signal(0);
  protected readonly tamanoPagina = signal(6);

  protected readonly respuesta = signal<PaginaResponse<PlantaBusquedaDTO> | null>(null);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly filtros = computed<FiltrosCatalogo>(() => ({
    nombre: this.nombre().trim() || undefined,
    etapa: this.etapa() || undefined,
    precioMin: this.precioMin() ?? undefined,
    precioMax: this.precioMax() ?? undefined,
  }));

  protected readonly ejemplares = computed(() => this.respuesta()?.contenido ?? []);
  protected readonly totalPaginas = computed(() => this.respuesta()?.totalPaginas ?? 0);
  protected readonly totalElementos = computed(() => this.respuesta()?.totalElementos ?? 0);
  protected readonly paginaVisible = computed(
    () => (this.respuesta()?.paginaActual ?? this.pagina()) + 1,
  );
  protected readonly hayPaginaAnterior = computed(() => this.pagina() > 0);
  protected readonly hayPaginaSiguiente = computed(
    () => this.pagina() + 1 < this.totalPaginas(),
  );

  constructor() {
    effect((onCleanup) => {
      const filtros = this.filtros();
      const page = this.pagina();
      const size = this.tamanoPagina();

      let suscripcion: Subscription | undefined;

      this.cargando.set(true);
      this.error.set(null);

      const temporizador = setTimeout(() => {
        suscripcion = this.catalogoService.buscarEjemplares(filtros, page, size).subscribe({
          next: (respuesta) => {
            this.respuesta.set(respuesta);
            this.cargando.set(false);
          },
          error: () => {
            this.respuesta.set(null);
            this.error.set(
              'No se pudo cargar el catálogo. Verifica la conexión con el servidor.',
            );
            this.cargando.set(false);
            this.avisoService.mostrarError('No se pudo consultar el catálogo de ejemplares.');
          },
        });
      }, 350);

      onCleanup(() => {
        clearTimeout(temporizador);
        suscripcion?.unsubscribe();
      });
    });
  }

  protected actualizarNombre(valor: string): void {
    this.nombre.set(valor);
    this.pagina.set(0);
  }

  protected actualizarEtapa(valor: string): void {
    this.etapa.set(valor as EtapaCrecimiento | '');
    this.pagina.set(0);
  }

  protected actualizarPrecioMin(valor: string): void {
    this.precioMin.set(this.aNumeroOpcional(valor));
    this.pagina.set(0);
  }

  protected actualizarPrecioMax(valor: string): void {
    this.precioMax.set(this.aNumeroOpcional(valor));
    this.pagina.set(0);
  }

  protected limpiarFiltros(): void {
    this.nombre.set('');
    this.etapa.set('');
    this.precioMin.set(null);
    this.precioMax.set(null);
    this.pagina.set(0);
  }

  protected anterior(): void {
    if (this.hayPaginaAnterior()) {
      this.pagina.update((pagina) => pagina - 1);
    }
  }

  protected siguiente(): void {
    if (this.hayPaginaSiguiente()) {
      this.pagina.update((pagina) => pagina + 1);
    }
  }

  protected iniciarPedido(ejemplar: PlantaBusquedaDTO): void {
    this.router.navigate(['/pedidos'], {
      queryParams: {
        ejemplarId: ejemplar.ejemplarId,
        precio: ejemplar.precio ?? 0,
      },
    });
  }

  private aNumeroOpcional(valor: string): number | null {
    if (valor.trim() === '') {
      return null;
    }
    const numero = Number(valor);
    return Number.isFinite(numero) ? numero : null;
  }
}
