import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CrearPedidoRequest, CancelarPedidoRequest, PedidoResponse } from '../modelos/pedido.model';

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/pedidos`;

  crearPedido(request: CrearPedidoRequest): Observable<PedidoResponse> {
    return this.http.post<PedidoResponse>(this.apiUrl, request);
  }

  cancelarPedido(request: CancelarPedidoRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/cancelar`, request);
  }
}