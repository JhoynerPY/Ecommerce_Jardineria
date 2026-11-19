import { Routes } from '@angular/router';
import { Inicio } from './componentes/inicio/inicio';
import { Login } from './componentes/login/login';
import { Registro } from './componentes/registro/registro';
import { Catalogo } from './componentes/catalogo/catalogo';
import { Pedidos } from './componentes/pedidos/pedidos'; 
import { noAuthGuard } from './guards/no-auth.guard';
import { authGuard } from './guards/auth.guard'; 

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'login', component: Login, canActivate: [noAuthGuard] },
  { path: 'registro', component: Registro, canActivate: [noAuthGuard] },
  { path: 'catalogo', component: Catalogo },
  { path: 'pedidos', component: Pedidos, canActivate: [authGuard] }, // nueva ruta de pedidos
  { path: '**', pathMatch: 'full', redirectTo: '' },
];