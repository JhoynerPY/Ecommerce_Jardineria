import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MetricaHome, BloqueValor } from '../../modelos/home.model';

@Component({
  standalone: true,
  imports: [CommonModule, RouterLink],
  selector: 'app-inicio',
  styleUrl: './inicio.css',
  templateUrl: './inicio.html',
})
export class Inicio {
  protected readonly metricas = signal<MetricaHome[]>([
    {
      icono: 'bi-tree-fill',
      etiqueta: 'viveros aliados',
      valor: '+45',
      color: 'bosque',
    },
    {
      icono: 'bi-truck',
      etiqueta: 'Entregas en menos de 24h',
      valor: '',
      color: 'bosque',
    },
    {
      icono: 'bi-star-fill',
      etiqueta: 'satisfacción',
      valor: '4.9/5',
      color: 'terracota',
    },
  ]);

  protected readonly bloquesValor = signal<BloqueValor[]>([
    {
      icono: 'bi-geo-alt-fill',
      titulo: 'Vivero Cercano Asignado',
      descripcion:
        'Tu pedido se despacha directamente desde el productor o vivero más próximo a tu ubicación geográfica, reduciendo tiempos de traslado y la huella de carbono.',
      sello: 'Rango de proximidad 15km',
      color: 'bosque',
    },
    {
      icono: 'bi-file-earmark-text-fill',
      titulo: 'Ficha de Cuidado Garantizada',
      descripcion:
        'Cada especie incluye una guía botánica detallada y personalizada elaborada por especialistas: frecuencia de riego, exposición solar y requerimientos de sustrato.',
      sello: 'Diagnóstico digital incluido',
      color: 'bosque',
    },
    {
      icono: 'bi-exclamation-triangle-fill',
      titulo: 'Alerta de Riesgo para Mascotas y Niños',
      descripcion:
        'Información clara sobre niveles de toxicidad en hojas o savia para perros, gatos y niños, permitiéndote elegir con total tranquilidad para tu hogar.',
      sello: 'Filtro Pet-Friendly activo',
      color: 'terracota',
    },
  ]);
}
