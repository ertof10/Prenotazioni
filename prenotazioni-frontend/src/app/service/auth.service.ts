import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AuthLoginRequest } from '../model/auth-login-request';
import { AuthLoginResponse } from '../model/auth-login-response';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) { }

  login(request: AuthLoginRequest): Observable<AuthLoginResponse> {
    return this.http.post<AuthLoginResponse>(`${this.apiUrl}/login`, request);
  }

  salvaSessione(response: AuthLoginResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('ruolo', response.ruolo);
    localStorage.setItem('email', response.email);

    if (response.idAccount !== null && response.idAccount !== undefined) {
      localStorage.setItem('idAccount', response.idAccount.toString());
    }

    if (response.idUtente !== null && response.idUtente !== undefined) {
      localStorage.setItem('idUtente', response.idUtente.toString());
    }

    if (response.idCollaboratore !== null && response.idCollaboratore !== undefined) {
      localStorage.setItem('idCollaboratore', response.idCollaboratore.toString());
    }
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('ruolo');
    localStorage.removeItem('email');
    localStorage.removeItem('idAccount');
    localStorage.removeItem('idUtente');
    localStorage.removeItem('idCollaboratore');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRuolo(): string | null {
    return localStorage.getItem('ruolo');
  }

  isAutenticato(): boolean {
    return this.getToken() !== null;
  }
}