import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { LoginRequest, RegisterRequest, AuthResponse, UsuarioAutenticado } from '../modelos/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = environment.apiUrl;

  private readonly TOKEN_KEY = 'token';

  public readonly estaAutenticado = signal<boolean>(false);
  public readonly rolActual = signal<string | null>(null);
  public readonly correoUsuario = signal<string | null>(null);

  readonly estaAutenticado$ = this.estaAutenticado;
  readonly rolActual$ = this.rolActual;
  readonly correoUsuario$ = this.correoUsuario;

  constructor() {
    this.restaurarSesionDesdeToken();
  }

  login(credenciales: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credenciales);
  }

  registrar(datos: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, datos);
  }

  guardarSesion(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    this.estaAutenticado.set(true);
    this.correoUsuario.set(response.email ?? null);
    const roles = this.extraerRolesDelToken(response.token);
    this.rolActual.set(roles.length > 0 ? roles[0] : null);
  }

  obtenerToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  obtenerPayloadSeguro(): UsuarioAutenticado | null {
    const token = this.obtenerToken();
    if (!token) return null;

    try {
      const payloadBase64 = token.split('.')[1];
      if (!payloadBase64) return null;

      const payloadJson = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'));
      const payload = JSON.parse(payloadJson);

      return {
        correo: payload.sub ?? payload.email ?? null,
        nombre: payload.nombre ?? null,
        roles: Array.isArray(payload.roles) ? payload.roles : [],
        token,
        tipoToken: payload.tipoToken ?? 'Bearer',
      };
    } catch {
      return null;
    }
  }

  extraerRolesDelToken(token: string): string[] {
    const payload = this.obtenerPayloadSeguroDesdeToken(token);
    return payload?.roles ?? [];
  }

  private obtenerPayloadSeguroDesdeToken(token: string): UsuarioAutenticado | null {
    try {
      const payloadBase64 = token.split('.')[1];
      if (!payloadBase64) return null;
      const payloadJson = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'));
      const payload = JSON.parse(payloadJson);
      return {
        correo: payload.sub ?? payload.email ?? null,
        nombre: payload.nombre ?? null,
        roles: Array.isArray(payload.roles) ? payload.roles : [],
        token,
        tipoToken: payload.tipoToken ?? 'Bearer',
      };
    } catch {
      return null;
    }
  }

  cerrarSesion(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.estaAutenticado.set(false);
    this.rolActual.set(null);
    this.correoUsuario.set(null);
    this.router.navigateByUrl('/login');
  }

  estaAutenticadoValor(): boolean {
    return this.estaAutenticado();
  }

  private restaurarSesionDesdeToken(): void {
    const token = this.obtenerToken();
    if (token) {
      const payload = this.obtenerPayloadSeguroDesdeToken(token);
      if (payload) {
        this.estaAutenticado.set(true);
        this.correoUsuario.set(payload.correo);
        this.rolActual.set(payload.roles.length > 0 ? payload.roles[0] : null);
      } else {
        localStorage.removeItem(this.TOKEN_KEY);
      }
    }
  }
}
