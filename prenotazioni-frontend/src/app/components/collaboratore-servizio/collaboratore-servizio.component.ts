import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { CollaboratoreServizio } from '../../model/collaboratore-servizio';
import { CollaboratoreServizioService } from '../../service/collaboratore-servizio.service';

@Component({
  selector: 'app-collaboratore-servizio',
  standalone: false,
  templateUrl: './collaboratore-servizio.component.html',
  styleUrl: './collaboratore-servizio.component.css'
})
export class CollaboratoreServizioComponent implements OnInit {

  associazioni: CollaboratoreServizio[] = [];
  caricamento: boolean = false;
  errore: string = '';

  constructor(
    private collaboratoreServizioService: CollaboratoreServizioService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.caricaAssociazioni();
  }

  caricaAssociazioni(): void {

    this.caricamento = true;
    this.errore = '';

    this.collaboratoreServizioService.stampaTutti().subscribe({
      next: (response) => {
        console.log('RISPOSTA COLLABORATORE-SERVIZIO:', response);

        this.associazioni = response.content;
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE CARICAMENTO COLLABORATORE-SERVIZIO:', error);

        this.errore = 'Errore durante il caricamento delle associazioni collaboratore-servizio';
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }
}