import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  ArticuloKitResponse,
  ConfigurarKitRequest,
  KitResponse,
} from '../modelos/kit.model';

@Injectable({ providedIn: 'root' })
export class KitService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/kits`;

  listarArticulos(): Observable<ArticuloKitResponse[]> {
    return this.http.get<ArticuloKitResponse[]>(`${this.apiUrl}/articulos`);
  }

  listarKits(): Observable<KitResponse[]> {
    return this.http.get<KitResponse[]>(this.apiUrl);
  }

  crearKit(request: ConfigurarKitRequest): Observable<KitResponse> {
    return this.http.post<KitResponse>(this.apiUrl, request);
  }
}