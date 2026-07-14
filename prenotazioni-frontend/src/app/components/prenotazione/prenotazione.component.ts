import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { Prenotazione } from '../../model/prenotazione';
import { PrenotazioneService } from '../../service/prenotazione.service';

@Component({
  selector: 'app-prenotazione',
  standalone: false,
  templateUrl: './prenotazione.component.html',
  styleUrl: './prenotazione.component.css'
})
export class PrenotazioneComponent implements OnInit {

  prenotazioni: Prenotazione[] = [];
  prenotazioneSelezionata?: Prenotazione;

  caricamento: boolean = false;
  errore: string = '';

  constructor(
    private prenotazioneService: PrenotazioneService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.caricaPrenotazioni();
  }

  caricaPrenotazioni(): void {

    this.caricamento = true;
    this.errore = '';

    this.prenotazioneService.stampaTutti().subscribe({
      next: (response) => {
        console.log('RISPOSTA PRENOTAZIONI:', response);

        this.prenotazioni = response.content;
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE CARICAMENTO PRENOTAZIONI:', error);

        this.errore = 'Errore durante il caricamento delle prenotazioni';
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  apriGestionePrenotazione(prenotazione: Prenotazione): void {
    this.prenotazioneSelezionata = prenotazione;
  }

  chiudiGestionePrenotazione(): void {
    this.prenotazioneSelezionata = undefined;
  }

  getCliente(prenotazione: Prenotazione): string {

    if (prenotazione.nomeUtente || prenotazione.cognomeUtente) {
      return `${prenotazione.nomeUtente || ''} ${prenotazione.cognomeUtente || ''}`.trim();
    }

    if (prenotazione.emailUtente) {
      return prenotazione.emailUtente;
    }

    return 'Cliente non valorizzato';
  }

  getCollaboratore(prenotazione: Prenotazione): string {

    if (prenotazione.nomeCollaboratore || prenotazione.cognomeCollaboratore) {
      return `${prenotazione.nomeCollaboratore || ''} ${prenotazione.cognomeCollaboratore || ''}`.trim();
    }

    return 'Non assegnato';
  }

  getServizio(prenotazione: Prenotazione): string {

    if (prenotazione.nomeServizio) {
      return prenotazione.nomeServizio;
    }

    return 'Servizio non valorizzato';
  }

  getClasseStato(statoPrenotazione: string): string {

    if (!statoPrenotazione) {
      return 'status-muted';
    }

    const stato = statoPrenotazione.toUpperCase();

    if (stato.includes('CONFERMATA') || stato.includes('COMPLETATA') || stato.includes('ATTIVA')) {
      return 'status-success';
    }

    if (stato.includes('ANNULLATA') || stato.includes('CANCELLATA')) {
      return 'status-danger';
    }

    if (stato.includes('RIPROGRAMMARE') || stato.includes('ATTESA') || stato.includes('PENDING')) {
      return 'status-warning';
    }

    return 'status-info';
  }
}