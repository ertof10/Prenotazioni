import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { UtenteService } from '../../service/utente.service';
import { CollaboratoreService } from '../../service/collaboratore.service';
import { ServizioService } from '../../service/servizio.service';
import { PrenotazioneService } from '../../service/prenotazione.service';
import { PreventivoService } from '../../service/preventivo.service';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class Dashboard implements OnInit {

  ruolo: string | null = localStorage.getItem('ruolo');

  totaleUtenti: number = 0;
  totaleCollaboratori: number = 0;
  totaleServizi: number = 0;
  totalePrenotazioni: number = 0;
  totalePreventivi: number = 0;

  caricamento: boolean = false;
  errore: string = '';

  constructor(
    private utenteService: UtenteService,
    private collaboratoreService: CollaboratoreService,
    private servizioService: ServizioService,
    private prenotazioneService: PrenotazioneService,
    private preventivoService: PreventivoService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.caricaDashboard();
  }

  caricaDashboard(): void {

    this.caricamento = true;
    this.errore = '';

    this.caricaTotaleUtenti();
    this.caricaTotaleCollaboratori();
    this.caricaTotaleServizi();
    this.caricaTotalePrenotazioni();
    this.caricaTotalePreventivi();

    this.caricamento = false;
    this.changeDetectorRef.detectChanges();
  }

  private caricaTotaleUtenti(): void {
    this.utenteService.stampaTutti().subscribe({
      next: (response) => {
        this.totaleUtenti = response.totalElements;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE DASHBOARD UTENTI:', error);
        this.errore = 'Errore durante il caricamento dei dati dashboard';
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  private caricaTotaleCollaboratori(): void {
    this.collaboratoreService.stampaTutti().subscribe({
      next: (response) => {
        this.totaleCollaboratori = response.totalElements;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE DASHBOARD COLLABORATORI:', error);
        this.errore = 'Errore durante il caricamento dei dati dashboard';
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  private caricaTotaleServizi(): void {
    this.servizioService.stampaTutti().subscribe({
      next: (response) => {
        this.totaleServizi = response.totalElements;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE DASHBOARD SERVIZI:', error);
        this.errore = 'Errore durante il caricamento dei dati dashboard';
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  private caricaTotalePrenotazioni(): void {
    this.prenotazioneService.stampaTutti().subscribe({
      next: (response) => {
        this.totalePrenotazioni = response.totalElements;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE DASHBOARD PRENOTAZIONI:', error);
        this.errore = 'Errore durante il caricamento dei dati dashboard';
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  private caricaTotalePreventivi(): void {
    this.preventivoService.stampaTutti().subscribe({
      next: (response) => {
        this.totalePreventivi = response.totalElements;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE DASHBOARD PREVENTIVI:', error);
        this.errore = 'Errore durante il caricamento dei dati dashboard';
        this.changeDetectorRef.detectChanges();
      }
    });
  }
}