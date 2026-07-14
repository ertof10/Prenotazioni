import { Component, OnInit } from '@angular/core';

interface ImpostazioniAttivitaHome {
  nomeAttivita: string;
  descrizioneAttivita: string;
  emailContatto: string;
  telefonoContatto: string;
  indirizzo: string;
  sitoWeb: string;
  urlLogo: string;
  urlImmagineHome: string;
  testoHome: string;
}

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  impostazioni?: ImpostazioniAttivitaHome;

  nomeVisualizzato: string = 'BookingPro';
  descrizioneVisualizzata: string = 'Gestionale prenotazioni professionale per aziende, studi e attività con appuntamenti.';
  testoHomeVisualizzato: string = 'Organizza clienti, staff, servizi e appuntamenti in un’unica piattaforma moderna.';
  urlLogoVisualizzato: string = '';
  urlImmagineHomeVisualizzata: string = '';

  ngOnInit(): void {
    this.caricaImpostazioniHome();
  }

  private caricaImpostazioniHome(): void {

    const impostazioniSalvate = localStorage.getItem('impostazioniAttivita');

    if (!impostazioniSalvate) {
      return;
    }

    this.impostazioni = JSON.parse(impostazioniSalvate);

    if (this.impostazioni?.nomeAttivita) {
      this.nomeVisualizzato = this.impostazioni.nomeAttivita;
    }

    if (this.impostazioni?.descrizioneAttivita) {
      this.descrizioneVisualizzata = this.impostazioni.descrizioneAttivita;
    }

    if (this.impostazioni?.testoHome) {
      this.testoHomeVisualizzato = this.impostazioni.testoHome;
    }

    if (this.impostazioni?.urlLogo) {
      this.urlLogoVisualizzato = this.impostazioni.urlLogo;
    }

    if (this.impostazioni?.urlImmagineHome) {
      this.urlImmagineHomeVisualizzata = this.impostazioni.urlImmagineHome;
    }
  }
}