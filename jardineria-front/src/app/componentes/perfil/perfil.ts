import { Component, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { PerfilService } from '../../servicios/perfil.service';
import { PedidoService } from '../../servicios/pedido.service';
import { KitService } from '../../servicios/kit.service';
import { AvisoService } from '../../servicios/aviso.service';
import { CarritoService } from '../../servicios/carrito.service';
import {
  PedidoResumenResponse,
  PerfilResponse,
} from '../../modelos/perfil.model';
import { KitResponse } from '../../modelos/kit.model';

@Component({
  standalone: true,
  imports: [DatePipe, DecimalPipe, RouterLink],
  selector: 'app-perfil',
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil {
  private readonly perfilService = inject(PerfilService);
  private readonly pedidoService = inject(PedidoService);
  private readonly kitService = inject(KitService);
  private readonly avisoService = inject(AvisoService);
  protected readonly carritoService = inject(CarritoService);

  protected readonly perfil = signal<PerfilResponse | null>(null);
  protected readonly pedidos = signal<PedidoResumenResponse[]>([]);
  protected readonly kits = signal<KitResponse[]>([]);
  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal<string | null>(null);
  protected readonly procesandoPago = signal<string | null>(null);
  protected readonly procesandoCancelacion = signal<string | null>(null);

  constructor() {
    this.cargarDatos();
  }

  protected puedePagar(estado: string): boolean {
    return estado === 'PENDIENTE_PAGO';
  }

  protected puedeCancelar(estado: string): boolean {
    return !['ENVIADO', 'ENTREGADO', 'CANCELADO', 'RECHAZADO'].includes(estado);
  }

  protected claseEstado(estado: string): string {
    switch (estado) {
      case 'PAGADO':
      case 'ENTREGADO':
        return 'text-bg-success';
      case 'ENVIADO':
        return 'text-bg-primary';
      case 'CANCELADO':
      case 'RECHAZADO':
        return 'text-bg-danger';
      default:
        return 'text-bg-warning';
    }
  }

  protected pagar(pedido: PedidoResumenResponse): void {
    if (this.procesandoPago()) {
      return;
    }

    this.procesandoPago.set(pedido.id);
    this.pedidoService
      .procesarPago({ idPedido: pedido.id, metodoPago: 'TARJETA' })
      .subscribe({
        next: () => {
          this.procesandoPago.set(null);
          this.avisoService.mostrarExito(`Pago procesado para el pedido ${pedido.id}.`);
          this.cargarPedidos();
          this.cargarPerfil();
        },
        error: (error: HttpErrorResponse) => {
          this.procesandoPago.set(null);
          const mensaje = this.traducirError(error);
          this.avisoService.mostrarError(mensaje);
        },
      });
  }

  protected cancelar(pedido: PedidoResumenResponse): void {
    if (this.procesandoCancelacion()) {
      return;
    }

    this.procesandoCancelacion.set(pedido.id);
    this.pedidoService
      .cancelarPedido({
        idPedido: pedido.id,
        motivoCancelacion: 'Solicitud del comprador',
      })
      .subscribe({
        next: () => {
          this.procesandoCancelacion.set(null);
          this.avisoService.mostrarExito(`Pedido ${pedido.id} cancelado.`);
          this.cargarPedidos();
          this.cargarPerfil();
        },
        error: (error: HttpErrorResponse) => {
          this.procesandoCancelacion.set(null);
          const mensaje = this.traducirError(error);
          this.avisoService.mostrarError(mensaje);
        },
      });
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

  private cargarDatos(): void {
    this.cargando.set(true);
    this.mensajeError.set(null);
    this.cargarPerfil();
    this.cargarPedidos();
    this.cargarKits();
    this.cargando.set(false);
  }

  private cargarPerfil(): void {
    this.perfilService.obtenerPerfil().subscribe({
      next: (perfil) => this.perfil.set(perfil),
      error: (error: HttpErrorResponse) => {
        this.mensajeError.set(this.traducirError(error));
        this.avisoService.mostrarError(this.traducirError(error));
      },
    });
  }

  private cargarPedidos(): void {
    this.pedidoService.listarMios().subscribe({
      next: (pedidos) => this.pedidos.set(pedidos),
      error: (error: HttpErrorResponse) => {
        this.mensajeError.set(this.traducirError(error));
        this.avisoService.mostrarError(this.traducirError(error));
      },
    });
  }

  private cargarKits(): void {
    this.kitService.listarMios().subscribe({
      next: (kits) => this.kits.set(kits),
      error: (error: HttpErrorResponse) => {
        this.avisoService.mostrarError(this.traducirError(error));
      },
    });
  }

  private traducirError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor.';
    }
    if (error.status === 401) {
      return 'Tu sesión venció. Inicia sesión nuevamente.';
    }
    if (error.status === 403) {
      return 'No tienes permisos para realizar esta operación.';
    }
    if (error.status === 404) {
      return 'El pedido o el recurso solicitado no existe.';
    }
    if (error.status === 422 || error.status === 400) {
      return this.extraerMensaje(error) || 'La operación no pudo completarse.';
    }
    return 'Ocurrió un error inesperado.';
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