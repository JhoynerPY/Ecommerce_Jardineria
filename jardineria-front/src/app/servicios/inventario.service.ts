import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  ActualizarEstadoSaludRequest,
  ActualizarEstadoSaludResponse,
} from '../modelos/inventario.model';

@Injectable({ providedIn: 'root' })
export class InventarioService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/inventario`;

  actualizarEstadoSalud(
    idEjemplar: string,
    request: ActualizarEstadoSaludRequest,
  ): Observable<ActualizarEstadoSaludResponse> {
    return this.http.put<ActualizarEstadoSaludResponse>(
      `${this.apiUrl}/ejemplar/${encodeURIComponent(idEjemplar)}/estado-salud`,
      request,
    );
  }
}
