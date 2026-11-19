import { Component, computed, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { Avisos } from './componentes/avisos/avisos';
import { AuthService } from './servicios/auth.service';
import { CarritoService } from './servicios/carrito.service';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Avisos],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly authService = inject(AuthService);
  protected readonly carritoService = inject(CarritoService);
  protected readonly titulo = signal('EcoJardín');
  protected readonly piePagina = signal('Universidad del Quindío - Programación Avanzada');

  protected readonly tieneAccesoVivero = computed(() => {
    const rol = (this.authService.rolActual() ?? '')
      .replace(/^ROLE_/i, '')
      .toUpperCase();
    return rol === 'VENDEDOR' || rol === 'VENDEDOR_VIVERO' || rol === 'ADMIN';
  });
}
