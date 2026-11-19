import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../servicios/auth.service';
import { AvisoService } from '../../servicios/aviso.service';
import { RegisterRequest } from '../../modelos/auth.model';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  selector: 'app-registro',
  styleUrl: './registro.css',
  templateUrl: './registro.html',
})
export class Registro {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly avisoService = inject(AvisoService);
  private readonly router = inject(Router);

  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal<string | null>(null);
  protected readonly mostrarPassword = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    rol: ['COMPRADOR', [Validators.required]],
  });

  protected get nombre() {
    return this.form.controls.nombre;
  }
  protected get email() {
    return this.form.controls.email;
  }
  protected get password() {
    return this.form.controls.password;
  }
  protected get rol() {
    return this.form.controls.rol;
  }

  protected alternarPassword(): void {
    this.mostrarPassword.update((v) => !v);
  }

  protected enviar(): void {
    this.mensajeError.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { nombre, email, password, rol } = this.form.getRawValue();
    const datos: RegisterRequest = {
      nombre,
      email,
      password,
      roles: [rol],
    };
    this.cargando.set(true);
    this.authService.registrar(datos).subscribe({
      next: (respuesta) => {
        this.authService.guardarSesion(respuesta);
        this.avisoService.mostrarExito(
          `Registro exitoso. Bienvenido, ${respuesta.nombre || respuesta.email}`
        );
        this.router.navigateByUrl('/catalogo');
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        const mensaje = this.traducirError(error);
        this.mensajeError.set(mensaje);
        this.avisoService.mostrarError(mensaje);
        this.cargando.set(false);
      },
    });
  }

  private traducirError(error: HttpErrorResponse): string {
    if (error.status === 409) {
      return 'Ya existe una cuenta registrada con ese correo electrónico.';
    }
    if (error.status === 400) {
      return 'Revisa los datos ingresados. La contraseña debe tener al menos 6 caracteres.';
    }
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. Intenta nuevamente.';
    }
    return 'Ocurrió un error inesperado. Intenta nuevamente.';
  }
}
