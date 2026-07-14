import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { Utente } from '../../model/utente';
import { UtenteService } from '../../service/utente.service';

@Component({
  selector: 'app-utente',
  standalone: false,
  templateUrl: './utente.component.html',
  styleUrl: './utente.component.css'
})
export class UtenteComponent implements OnInit {

  utenti: Utente[] = [];
  caricamento: boolean = false;
  errore: string = '';

  constructor(
    private utenteService: UtenteService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.caricaUtenti();
  }

  caricaUtenti(): void {

    this.caricamento = true;
    this.errore = '';

    this.utenteService.stampaTutti().subscribe({
      next: (response) => {
        console.log('RISPOSTA UTENTI:', response);

        this.utenti = response.content;
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE CARICAMENTO UTENTI:', error);

        this.errore = 'Errore durante il caricamento degli utenti';
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }
}