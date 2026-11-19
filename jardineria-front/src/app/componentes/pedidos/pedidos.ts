import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { PedidoService } from '../../servicios/pedido.service';
import { AvisoService } from '../../servicios/aviso.service';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule], // Quité RouterLink
  selector: 'app-pedidos',
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css'
})
export class Pedidos {
  private readonly fb = inject(FormBuilder);
  private readonly pedidoService = inject(PedidoService);
  private readonly avisoService = inject(AvisoService);
  private readonly router = inject(Router);

  protected readonly cargando = signal(false);
  protected readonly exencionSeleccionada = signal<string>('SIN_EXENCION');

  protected readonly form = this.fb.nonNullable.group({
    direccion: ['', [Validators.required, Validators.minLength(10)]],
    tipoExencion: ['SIN_EXENCION', [Validators.required]],
    aceptaExencion: [true]
  });

  private readonly idEjemplarPrueba = 'especie-001'; 
  private readonly precioPrueba = 25000;

  protected get direccion() { return this.form.controls.direccion; }

  protected actualizarExencion(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.exencionSeleccionada.set(select.value);
    if (select.value === 'SIN_EXENCION') {
      this.form.controls.aceptaExencion.setValue(true);
    }
  }

  protected enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    const formValues = this.form.getRawValue();
    const idCliente = 'cliente-001'; 

    const request = {
      idCliente: idCliente,
      detalles: [
        {
          idProducto: this.idEjemplarPrueba,
          cantidad: 1,
          precioUnitario: this.precioPrueba
        }
      ],
      direccion: formValues.direccion,
      tipoExencion: formValues.tipoExencion as any,
      terminosExencion: 'Acepto los términos de transporte de Raíz Local',
      versionExencion: '1.0',
      exencionAceptada: formValues.aceptaExencion
    };

    // Aquí le puse : any a respuesta para evitar el error de TS
    this.pedidoService.crearPedido(request).subscribe({
      next: (respuesta: any) => { 
        this.avisoService.mostrarExito(`Pedido creado con éxito. ID: ${respuesta.idPedido}`);
        this.cargando.set(false);
        this.router.navigateByUrl('/catalogo'); 
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        if (error.status === 422) {
          this.avisoService.mostrarError('El vivero no cubre esta dirección o los datos son inválidos.');
        } else if (error.status === 400) {
          this.avisoService.mostrarError('Revisa los datos ingresados en el formulario.');
        } else {
          this.avisoService.mostrarError('Error inesperado al crear el pedido.');
        }
      }
    });
  }
}