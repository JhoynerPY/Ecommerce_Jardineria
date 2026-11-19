import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvisoService } from '../../servicios/aviso.service';

@Component({
  standalone: true,
  imports: [CommonModule],
  selector: 'app-avisos',
  styleUrl: './avisos.css',
  templateUrl: './avisos.html',
})
export class Avisos {
  protected readonly avisosService = inject(AvisoService);
}
