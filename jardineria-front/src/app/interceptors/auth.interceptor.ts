import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../servicios/auth.service';
import { AvisoService } from '../servicios/aviso.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const avisoService = inject(AvisoService);
  const router = inject(Router);

  const token = authService.obtenerToken();
  const esPeticionLogin = req.url.includes('/auth/login');

  const peticion = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !esPeticionLogin) {
        authService.cerrarSesion();
        avisoService.mostrarError(
          'Tu sesión ha vencido o no es válida. Inicia sesión nuevamente.'
        );
        router.navigateByUrl('/login');
      }
      return throwError(() => error);
    })
  );
};
