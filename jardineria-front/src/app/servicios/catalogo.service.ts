import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { FiltrosCatalogo, PaginaResponse, PlantaBusquedaDTO } from '../modelos/catalogo.model';

@Injectable({ providedIn: 'root' })
export class CatalogoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  buscarEjemplares(
    filtros: FiltrosCatalogo = {},
    page = 0,
    size = 10,
  ): Observable<PaginaResponse<PlantaBusquedaDTO>> {
    let params = new HttpParams().set('page', page).set('size', size);

    const nombre = filtros.nombre?.trim();
    if (nombre) {
      params = params.set('nombre', nombre);
    }
    if (filtros.precioMin !== undefined) {
      params = params.set('precioMin', filtros.precioMin);
    }
    if (filtros.precioMax !== undefined) {
      params = params.set('precioMax', filtros.precioMax);
    }
    if (filtros.etapa) {
      params = params.set('etapa', filtros.etapa);
    }

    return this.http.get<PaginaResponse<PlantaBusquedaDTO>>(
      `${this.apiUrl}/catalogo/ejemplares`,
      { params },
    );
  }
}
