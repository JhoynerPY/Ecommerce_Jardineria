import { Component, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { PedidoService } from '../../servicios/pedido.service';
import { AvisoService } from '../../servicios/aviso.service';
import { CarritoService } from '../../servicios/carrito.service';
import {
  CrearPedidoRequest,
  CrearPedidoResponse,
  DetallePedidoRequest,
  TipoExencion,
} from '../../modelos/pedido.model';

@Component({
  standalone: true,
  imports: [DecimalPipe, ReactiveFormsModule, RouterLink],
  selector: 'app-pedidos',
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css',
})
export class Pedidos {
  private readonly fb = inject(FormBuilder);
  private readonly pedidoService = inject(PedidoService);
  private readonly avisoService = inject(AvisoService);
  protected readonly carritoService = inject(CarritoService);

  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal<string | null>(null);
  protected readonly respuesta = signal<CrearPedidoResponse | null>(null);
  protected readonly exencionSeleccionada = signal<TipoExencion>('SIN_EXENCION');

  protected readonly form = this.fb.group({
    direccion: this.fb.nonNullable.control('', [
      Validators.required,
      Validators.minLength(10),
    ]),
    tipoExencion: this.fb.nonNullable.control<TipoExencion>('SIN_EXENCION', [
      Validators.required,
    ]),
    porcentajeExencion: this.fb.control<number | null>(null),
    aceptaExencion: this.fb.nonNullable.control(true),
  });

  constructor() {
    this.form.controls.tipoExencion.valueChanges.subscribe((tipo) => {
      this.exencionSeleccionada.set(tipo);
      this.actualizarValidadoresExencion(tipo);
    });
  }

  protected get direccion() {
    return this.form.controls.direccion;
  }

  protected get porcentajeExencion() {
    return this.form.controls.porcentajeExencion;
  }

  protected get aceptaExencion() {
    return this.form.controls.aceptaExencion;
  }

  protected enviar(): void {
    if (this.cargando()) {
      return;
    }

    const detalles = this.construirDetalles();
    if (detalles.length === 0) {
      this.mensajeError.set('Agrega ejemplares o kits al carrito antes de finalizar.');
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.mensajeError.set('Revisa los campos marcados antes de confirmar.');
      return;
    }

    const valores = this.form.getRawValue();
    const tipoExencion = valores.tipoExencion;
    const porcentaje =
      tipoExencion === 'ACUERDO_PARCIAL_PERDIDA'
        ? Number(valores.porcentajeExencion)
        : null;

    const request: CrearPedidoRequest = {
      detalles,
      direccion: valores.direccion.trim(),
      tipoExencion,
      porcentajeExencion: porcentaje,
      terminosExencion: this.construirTerminosExencion(tipoExencion, porcentaje),
      versionExencion: '1.0',
      exencionAceptada:
        tipoExencion === 'SIN_EXENCION' ? true : valores.aceptaExencion,
    };

    this.cargando.set(true);
    this.mensajeError.set(null);
    this.respuesta.set(null);

    this.pedidoService.crearPedido(request).subscribe({
      next: (respuesta) => {
        this.respuesta.set(respuesta);
        this.cargando.set(false);
        this.carritoService.vaciar();
        this.avisoService.mostrarExito(
          `Pedido creado exitosamente. ID: ${respuesta.idPedido}`,
        );
      },
      error: (error: HttpErrorResponse) => {
        const mensaje = this.traducirError(error);
        this.mensajeError.set(mensaje);
        this.cargando.set(false);
        this.avisoService.mostrarError(mensaje);
      },
    });
  }

  private construirDetalles(): DetallePedidoRequest[] {
    const ejemplares: DetallePedidoRequest[] = this.carritoService.ejemplares().map(
      (ejemplar) => ({
        idProducto: ejemplar.ejemplarId,
        cantidad: ejemplar.cantidad,
        precioUnitario: ejemplar.precio,
      }),
    );

    const kits: DetallePedidoRequest[] = this.carritoService.kits().map((kit) => ({
      idProducto: kit.kitId,
      cantidad: kit.cantidad,
      precioUnitario: kit.precioConDescuento,
    }));

    return [...ejemplares, ...kits];
  }

  private actualizarValidadoresExencion(tipo: TipoExencion): void {
    if (tipo === 'ACUERDO_PARCIAL_PERDIDA') {
      this.porcentajeExencion.setValidators([
        Validators.required,
        Validators.min(1),
        Validators.max(100),
      ]);
    } else {
      this.porcentajeExencion.clearValidators();
      this.porcentajeExencion.setValue(null);
    }
    this.porcentajeExencion.updateValueAndValidity({ emitEvent: false });

    if (tipo === 'SIN_EXENCION') {
      this.aceptaExencion.setValue(true);
      this.aceptaExencion.clearValidators();
    } else {
      this.aceptaExencion.setValue(false);
      this.aceptaExencion.setValidators([Validators.requiredTrue]);
    }
    this.aceptaExencion.updateValueAndValidity({ emitEvent: false });
  }

  private construirTerminosExencion(
    tipo: TipoExencion,
    porcentaje: number | null,
  ): string {
    switch (tipo) {
      case 'EXENCION_TOTAL':
        return 'El cliente exime al vivero de toda responsabilidad por daños durante el transporte.';
      case 'ACUERDO_PARCIAL_PERDIDA':
        return `Acuerdo parcial del ${porcentaje}% de responsabilidad por daños durante el transporte.`;
      default:
        return 'Sin exención de responsabilidad aplicada.';
    }
  }

  private traducirError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. Verifica tu conexión e intenta nuevamente.';
    }
    if (error.status === 400) {
      return this.extraerMensaje(error) || 'Revisa los datos ingresados en el formulario.';
    }
    if (error.status === 403) {
      return 'No tienes permisos para crear pedidos.';
    }
    if (error.status === 404) {
      return 'El ejemplar o el cliente asociado no existe.';
    }
    if (error.status === 422) {
      return (
        this.extraerMensaje(error) ||
        'El vivero no cubre la dirección indicada o los datos del pedido no son válidos.'
      );
    }
    return 'Ocurrió un error inesperado al crear el pedido.';
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