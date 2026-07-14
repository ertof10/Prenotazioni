import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit {

  ruolo: string | null = null;

  constructor(
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.ruolo = this.authService.getRuolo();
  }

  logout(): void {
    this.authService.logout();

    this.router.navigate(['/login']);
  }
}