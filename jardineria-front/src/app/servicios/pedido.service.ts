import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  CancelarPedidoRequest,
  CancelarPedidoResponse,
  CrearPedidoRequest,
  CrearPedidoResponse,
  ProcesarCompraRequest,
  ProcesarCompraResponse,
} from '../modelos/pedido.model';
import { PedidoResumenResponse } from '../modelos/perfil.model';

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/pedidos`;
  private readonly pagosUrl = `${environment.apiUrl}/pagos`;

  crearPedido(request: CrearPedidoRequest): Observable<CrearPedidoResponse> {
    return this.http.post<CrearPedidoResponse>(this.apiUrl, request);
  }

  listarMios(): Observable<PedidoResumenResponse[]> {
    return this.http.get<PedidoResumenResponse[]>(`${this.apiUrl}/mios`);
  }

  cancelarPedido(request: CancelarPedidoRequest): Observable<CancelarPedidoResponse> {
    return this.http.post<CancelarPedidoResponse>(`${this.apiUrl}/cancelar`, request);
  }

  procesarPago(request: ProcesarCompraRequest): Observable<ProcesarCompraResponse> {
    return this.http.post<ProcesarCompraResponse>(`${this.pagosUrl}/procesar`, request);
  }
}
