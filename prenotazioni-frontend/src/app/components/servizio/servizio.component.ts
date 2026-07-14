import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { Servizio } from '../../model/servizio';
import { ServizioService } from '../../service/servizio.service';

@Component({
  selector: 'app-servizio',
  standalone: false,
  templateUrl: './servizio.component.html',
  styleUrl: './servizio.component.css'
})
export class ServizioComponent implements OnInit {

  servizi: Servizio[] = [];
  caricamento: boolean = false;
  errore: string = '';

  constructor(
    private servizioService: ServizioService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.caricaServizi();
  }

  caricaServizi(): void {

    this.caricamento = true;
    this.errore = '';

    this.servizioService.stampaTutti().subscribe({
      next: (response) => {
        this.servizi = response.content;
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE CARICAMENTO SERVIZI:', error);

        this.errore = 'Errore durante il caricamento dei servizi';
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }
}