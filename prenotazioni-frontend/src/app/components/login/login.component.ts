import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../service/auth.service';
import { AuthLoginResponse } from '../../model/auth-login-response';
@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class Login {

  email: string = 'admin@prenotazioni.local';
  password: string = 'password';

  rispostaLogin?: AuthLoginResponse;
  erroreLogin: string = '';

 constructor(
  private authService: AuthService,
  private router: Router
) { }

  login(): void {

    console.log('CLICK LOGIN');

    this.erroreLogin = '';
    this.rispostaLogin = undefined;

    this.authService.login({
      email: this.email,
      password: this.password
    }).subscribe({
     next: (response) => {
  console.log('RISPOSTA LOGIN:', response);

  this.rispostaLogin = response;
  this.authService.salvaSessione(response);
 
  this.router.navigate(['/dashboard']);
},
      error: (error) => {
        console.error('ERRORE LOGIN:', error);
        this.erroreLogin = 'Login fallito';
      }
    });
  }
}