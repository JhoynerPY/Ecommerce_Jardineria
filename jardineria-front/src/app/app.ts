import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { Avisos } from './componentes/avisos/avisos';
import { AuthService } from './servicios/auth.service';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Avisos],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly authService = inject(AuthService);
  protected readonly titulo = signal('EcoJardín');
  protected readonly piePagina = signal('Universidad del Quindío - Programación Avanzada');
}
