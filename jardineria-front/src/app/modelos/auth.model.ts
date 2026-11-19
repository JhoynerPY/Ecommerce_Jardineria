export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  nombre: string;
  roles: string[];
}

export interface AuthResponse {
  token: string;
  email: string;
  nombre: string;
  roles: string[];
  tipoToken: string;
}

export interface UsuarioAutenticado {
  correo: string;
  nombre: string;
  roles: string[];
  token: string;
  tipoToken: string;
}
