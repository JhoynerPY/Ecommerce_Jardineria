import { Component, computed, inject, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { InventarioService } from '../../servicios/inventario.service';
import { AvisoService } from '../../servicios/aviso.service';
import {
  ActualizarEstadoSaludResponse,
  EstadoDeSalud,
  OpcionEstadoSalud,
} from '../../modelos/inventario.model';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  selector: 'app-inventario-vivero',
  templateUrl: './inventario-vivero.html',
  styleUrl: './inventario-vivero.css',
})
export class InventarioVivero {
  private readonly fb = inject(FormBuilder);
  private readonly inventarioService = inject(InventarioService);
  private readonly avisoService = inject(AvisoService);
  private readonly route = inject(ActivatedRoute);

  protected readonly estados: OpcionEstadoSalud[] = [
    {
      valor: 'SANO',
      etiqueta: 'Sano',
      descripcion: 'El ejemplar se encuentra en buen estado y sigue disponible para venta.',
    },
    {
      valor: 'EN_RECUPERACION',
      etiqueta: 'En recuperación',
      descripcion: 'El ejemplar está en tratamiento y requiere seguimiento del vivero.',
    },
    {
      valor: 'MARCHITO',
      etiqueta: 'Marchito',
      descripcion: 'El ejemplar se retira automáticamente de la venta por su estado.',
    },
    {
      valor: 'CON_PLAGA',
      etiqueta: 'Con plaga',
      descripcion: 'El ejemplar se retira automáticamente de la venta y requiere aislamiento.',
    },
  ];

  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal<string | null>(null);
  protected readonly respuesta = signal<ActualizarEstadoSaludResponse | null>(null);
  protected readonly estadoSeleccionado = signal<EstadoDeSalud>('SANO');

  protected readonly form = this.fb.group({
    idEjemplar: this.fb.nonNullable.control('', [Validators.required]),
    nuevoEstado: this.fb.nonNullable.control<EstadoDeSalud>('SANO', [
      Validators.required,
    ]),
  });

  protected readonly opcionSeleccionada = computed(
    () =>
      this.estados.find(
        (estado) => estado.valor === this.estadoSeleccionado(),
      ) ?? this.estados[0],
  );

  constructor() {
    this.form.controls.nuevoEstado.valueChanges.subscribe((estado) => {
      this.estadoSeleccionado.set(estado);
    });

    this.route.queryParamMap.subscribe((params) => {
      const ejemplarId = params.get('ejemplarId');
      if (ejemplarId) {
        this.form.controls.idEjemplar.setValue(ejemplarId);
      }
    });
  }

  protected get idEjemplar() {
    return this.form.controls.idEjemplar;
  }

  protected enviar(): void {
    if (this.cargando()) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.mensajeError.set('Ingresa el ID del ejemplar y selecciona un estado.');
      return;
    }

    const { idEjemplar, nuevoEstado } = this.form.getRawValue();
    this.cargando.set(true);
    this.mensajeError.set(null);
    this.respuesta.set(null);

    this.inventarioService
      .actualizarEstadoSalud(idEjemplar.trim(), {
        idEjemplar: idEjemplar.trim(),
        nuevoEstado,
      })
      .subscribe({
        next: (respuesta) => {
          this.respuesta.set(respuesta);
          this.cargando.set(false);
          this.avisoService.mostrarExito(respuesta.mensaje);
        },
        error: (error: HttpErrorResponse) => {
          const mensaje = this.traducirError(error);
          this.mensajeError.set(mensaje);
          this.cargando.set(false);
          this.avisoService.mostrarError(mensaje);
        },
      });
  }

  private traducirError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. Verifica tu conexión e intenta nuevamente.';
    }
    if (error.status === 400) {
      return this.extraerMensaje(error) || 'Revisa el ID y el estado seleccionado.';
    }
    if (error.status === 403) {
      return 'No tienes permisos para actualizar el inventario del vivero.';
    }
    if (error.status === 404) {
      return 'No existe un ejemplar con el ID indicado.';
    }
    if (error.status === 422) {
      return this.extraerMensaje(error) || 'El estado de salud no es válido para el ejemplar.';
    }
    return 'Ocurrió un error inesperado al actualizar el estado de salud.';
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
