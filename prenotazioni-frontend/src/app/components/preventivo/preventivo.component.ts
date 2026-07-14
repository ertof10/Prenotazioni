import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { Preventivo } from '../../model/preventivo';
import { PreventivoService } from '../../service/preventivo.service';

@Component({
  selector: 'app-preventivo',
  standalone: false,
  templateUrl: './preventivo.component.html',
  styleUrl: './preventivo.component.css'
})
export class PreventivoComponent implements OnInit {

  preventivi: Preventivo[] = [];
  caricamento: boolean = false;
  errore: string = '';

  constructor(
    private preventivoService: PreventivoService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.caricaPreventivi();
  }

  caricaPreventivi(): void {

    this.caricamento = true;
    this.errore = '';

    this.preventivoService.stampaTutti().subscribe({
      next: (response) => {
        console.log('RISPOSTA PREVENTIVI:', response);

        this.preventivi = response.content;
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('ERRORE CARICAMENTO PREVENTIVI:', error);

        this.errore = 'Errore durante il caricamento dei preventivi';
        this.caricamento = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }
}