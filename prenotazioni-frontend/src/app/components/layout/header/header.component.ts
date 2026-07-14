import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  email: string | null = localStorage.getItem('email');
  ruolo: string | null = localStorage.getItem('ruolo');

}