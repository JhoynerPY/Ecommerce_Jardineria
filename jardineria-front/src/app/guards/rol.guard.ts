import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../servicios/auth.service';
import { AvisoService } from '../servicios/aviso.service';

const normalizarRol = (rol: string): string => {
  const limpio = rol.replace(/^ROLE_/i, '').toUpperCase();
  return limpio === 'VENDEDOR' ? 'VENDEDOR_VIVERO' : limpio;
};

export const rolGuard = (rolesPermitidos: string[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const avisoService = inject(AvisoService);
    const router = inject(Router);

    const rolActual = authService.rolActual();
    const permitidos = rolesPermitidos.map(normalizarRol);

    if (rolActual && permitidos.includes(normalizarRol(rolActual))) {
      return true;
    }

    avisoService.mostrarError(
      'No tienes permisos para acceder a esta sección.'
    );
    return router.createUrlTree(['/catalogo']);
  };
};
