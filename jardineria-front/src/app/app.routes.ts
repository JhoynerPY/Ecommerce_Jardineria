import { Routes } from '@angular/router';
import { Inicio } from './componentes/inicio/inicio';
import { Login } from './componentes/login/login';
import { Registro } from './componentes/registro/registro';
import { Catalogo } from './componentes/catalogo/catalogo';
import { Pedidos } from './componentes/pedidos/pedidos'; 
import { Kits } from './componentes/kits/kits';
import { InventarioVivero } from './componentes/inventario-vivero/inventario-vivero';
import { noAuthGuard } from './guards/no-auth.guard';
import { authGuard } from './guards/auth.guard'; 
import { rolGuard } from './guards/rol.guard';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'login', component: Login, canActivate: [noAuthGuard] },
  { path: 'registro', component: Registro, canActivate: [noAuthGuard] },
  { path: 'catalogo', component: Catalogo },
  { path: 'pedidos', component: Pedidos, canActivate: [authGuard] }, // nueva ruta de pedidos
  { path: 'kits', component: Kits, canActivate: [authGuard] },
  {
    path: 'inventario-vivero',
    component: InventarioVivero,
    canActivate: [authGuard, rolGuard(['VENDEDOR', 'ADMIN'])],
  },
  { path: '**', pathMatch: 'full', redirectTo: '' },
];