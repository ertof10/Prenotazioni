import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { Collaboratore } from '../../model/collaboratore';
import { CollaboratoreService } from '../../service/collaboratore.service';

import { CollaboratoreServizio } from '../../model/collaboratore-servizio';
import { CollaboratoreServizioService } from '../../service/collaboratore-servizio.service';

import { AssenzaCollaboratore } from '../../model/assenza-collaboratore';
import { AssenzaCollaboratoreService } from '../../service/assenza-collaboratore.service';

import { Prenotazione } from '../../model/prenotazione';
import { PrenotazioneService } from '../../service/prenotazione.service';

@Component({
  selector: 'app-collaboratore',
  standalone: false,
  templateUrl: './collaboratore.component.html',
  styleUrl: './collaboratore.component.css'
})
export class CollaboratoreComponent implements OnInit {

  collaboratori: Collaboratore[] = [];
  collaboratoreSelezionato?: Collaboratore;

  serviziAssociati: CollaboratoreServizio[] = [];
  assenzeCollaboratore: AssenzaCollaboratore[] = [];
  appuntamentiCollaboratore: Prenotazione[] = [];

  tabAttiva: string = 'profilo';
  vistaCollaboratori: string = 'card';

  caricamento: boolean = false;
  caricamentoDettaglio: boolean = false;
  errore: string = '';
  erroreDettaglio: string = '';

  constructor(
    private collaboratoreService: CollaboratoreService,
    private collaboratoreServizioService: CollaboratoreServizioService,
    private assenzaCollaboratoreService: AssenzaCollaboratoreService,
    private prenotazioneService: PrenotazioneService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.caricaCollaboratori();
  }

  caricaCollaboratori(): void {

    this.caricamento = true;
    this.errore = '';

    this.collaboratoreService.stampaTutti().subscribe({
      next: (response) => {
        console.log('RISPOSTA COLLABORATORI:', response);

        this.collaboratori = response.content;
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE CARICAMENTO COLLABORATORI:', error);

        this.errore = 'Errore durante il caricamento dello staff';
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  cambiaVistaCollaboratori(vista: string): void {
    this.vistaCollaboratori = vista;
  }

  inizialiCollaboratore(collaboratore: Collaboratore): string {
    const nome = collaboratore.nomeCollaboratore ? collaboratore.nomeCollaboratore.charAt(0) : '';
    const cognome = collaboratore.cognomeCollaboratore ? collaboratore.cognomeCollaboratore.charAt(0) : '';

    return `${nome}${cognome}`.toUpperCase();
  }

  apriGestioneCollaboratore(collaboratore: Collaboratore): void {

    this.collaboratoreSelezionato = collaboratore;
    this.tabAttiva = 'profilo';

    this.serviziAssociati = [];
    this.assenzeCollaboratore = [];
    this.appuntamentiCollaboratore = [];

    this.erroreDettaglio = '';
    this.caricamentoDettaglio = true;

    this.caricaServiziAssociati(collaboratore.idCollaboratore);
    this.caricaAssenzeCollaboratore(collaboratore.idCollaboratore);
    this.caricaAppuntamentiCollaboratore(collaboratore.idCollaboratore);
  }

  chiudiGestioneCollaboratore(): void {
    this.collaboratoreSelezionato = undefined;
    this.serviziAssociati = [];
    this.assenzeCollaboratore = [];
    this.appuntamentiCollaboratore = [];
    this.erroreDettaglio = '';
    this.tabAttiva = 'profilo';
  }

  cambiaTab(tab: string): void {
    this.tabAttiva = tab;
  }

  private caricaServiziAssociati(idCollaboratore: number): void {

    this.collaboratoreServizioService.stampaServiziPerCollaboratore(idCollaboratore).subscribe({
      next: (response) => {
        console.log('RISPOSTA SERVIZI ASSOCIATI COLLABORATORE:', response);

        this.serviziAssociati = this.estraiContenuto(response);
        this.caricamentoDettaglio = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE SERVIZI ASSOCIATI COLLABORATORE:', error);

        this.erroreDettaglio = 'Errore durante il caricamento dei servizi associati allo staff';
        this.caricamentoDettaglio = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  private caricaAssenzeCollaboratore(idCollaboratore: number): void {

    this.assenzaCollaboratoreService.stampaPerCollaboratore(idCollaboratore).subscribe({
      next: (response) => {
        console.log('RISPOSTA ASSENZE COLLABORATORE SELEZIONATO:', response);

        this.assenzeCollaboratore = this.estraiContenuto(response);
        this.caricamentoDettaglio = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE ASSENZE COLLABORATORE SELEZIONATO:', error);

        this.erroreDettaglio = 'Errore durante il caricamento delle assenze dello staff';
        this.caricamentoDettaglio = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  private caricaAppuntamentiCollaboratore(idCollaboratore: number): void {

    this.prenotazioneService.stampaTutti().subscribe({
      next: (response) => {
        console.log('RISPOSTA APPUNTAMENTI COLLABORATORE:', response);

        const oggi = new Date();
        oggi.setHours(0, 0, 0, 0);

        this.appuntamentiCollaboratore = response.content.filter((prenotazione) => {

          if (prenotazione.idCollaboratore !== idCollaboratore) {
            return false;
          }

          if (!prenotazione.dataPrenotazione) {
            return false;
          }

          const dataPrenotazione = new Date(prenotazione.dataPrenotazione);
          dataPrenotazione.setHours(0, 0, 0, 0);

          return dataPrenotazione >= oggi;
        });

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE APPUNTAMENTI COLLABORATORE:', error);

        this.erroreDettaglio = 'Errore durante il caricamento degli appuntamenti dello staff';

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  private estraiContenuto(response: any): any[] {

    if (Array.isArray(response)) {
      return response;
    }

    if (response && Array.isArray(response.content)) {
      return response.content;
    }

    return [];
  }
}