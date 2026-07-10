import { Component } from '@angular/core';
import { AuthService, AuthLoginResponse } from './auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.css',
  standalone: false
})
export class App {

  email: string = 'admin@prenotazioni.local';
  password: string = 'password';

  rispostaLogin?: AuthLoginResponse;
  erroreLogin: string = '';

  constructor(private authService: AuthService) { }

  login(): void {

  this.erroreLogin = '';
  this.rispostaLogin = undefined;

  this.authService.login({
    email: this.email,
    password: this.password
  }).subscribe({
    next: (response) => {
      console.log('RISPOSTA LOGIN:', response);

      alert('LOGIN RIUSCITO');

      this.rispostaLogin = response;

      localStorage.setItem('token', response.token);
      localStorage.setItem('ruolo', response.ruolo);
      localStorage.setItem('email', response.email);
    },
    error: (error) => {
      console.error('ERRORE LOGIN:', error);
      this.erroreLogin = 'Login fallito';
    }
  });
}}