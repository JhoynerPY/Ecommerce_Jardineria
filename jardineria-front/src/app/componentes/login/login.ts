import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../servicios/auth.service';
import { AvisoService } from '../../servicios/aviso.service';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly avisoService = inject(AvisoService);
  private readonly router = inject(Router);

  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal<string | null>(null);
  protected readonly mostrarPassword = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  protected get email() {
    return this.form.controls.email;
  }
  protected get password() {
    return this.form.controls.password;
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
    this.cargando.set(true);
    this.authService.login(this.form.getRawValue()).subscribe({
      next: (respuesta) => {
        this.authService.guardarSesion(respuesta);
        this.avisoService.mostrarExito(
          `Bienvenido, ${respuesta.nombre || respuesta.email}`
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
    if (error.status === 401) {
      return 'Correo o contraseña incorrectos. Verifica tus credenciales.';
    }
    if (error.status === 400) {
      return 'Datos inválidos. Revisa el correo y la contraseña ingresados.';
    }
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. Intenta nuevamente.';
    }
    return 'Ocurrió un error inesperado. Intenta nuevamente.';
  }
}
