import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../servicios/auth.service';
import { AvisoService } from '../servicios/aviso.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const avisoService = inject(AvisoService);
  const router = inject(Router);

  if (authService.estaAutenticado()) {
    return true;
  }

  avisoService.mostrarError(
    'Debes iniciar sesión para acceder a esta sección.'
  );
  return router.createUrlTree(['/login']);
};
