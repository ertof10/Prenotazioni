import { Component, OnInit } from '@angular/core';

interface ImpostazioniAttivita {
  nomeAttivita: string;
  descrizioneAttivita: string;
  emailContatto: string;
  telefonoContatto: string;
  indirizzo: string;
  sitoWeb: string;
  urlLogo: string;
  urlImmagineHome: string;
  testoHome: string;
  anticipoMinimoPrenotazioneOre: number;
  limiteAnnullamentoOre: number;
  durataSlotMinuti: number;
}

@Component({
  selector: 'app-impostazioni-attivita',
  standalone: false,
  templateUrl: './impostazioni-attivita.component.html',
  styleUrl: './impostazioni-attivita.component.css'
})
export class ImpostazioniAttivitaComponent implements OnInit {

  messaggioSalvataggio: string = '';

  impostazioni: ImpostazioniAttivita = {
    nomeAttivita: '',
    descrizioneAttivita: '',
    emailContatto: '',
    telefonoContatto: '',
    indirizzo: '',
    sitoWeb: '',
    urlLogo: '',
    urlImmagineHome: '',
    testoHome: '',
    anticipoMinimoPrenotazioneOre: 24,
    limiteAnnullamentoOre: 12,
    durataSlotMinuti: 30
  };

  ngOnInit(): void {
    this.caricaImpostazioni();
  }

  salvaImpostazioni(): void {
    localStorage.setItem(
      'impostazioniAttivita',
      JSON.stringify(this.impostazioni)
    );

    this.messaggioSalvataggio = 'Impostazioni attività salvate provvisoriamente';

    setTimeout(() => {
      this.messaggioSalvataggio = '';
    }, 3000);
  }

  private caricaImpostazioni(): void {
    const impostazioniSalvate = localStorage.getItem('impostazioniAttivita');

    if (impostazioniSalvate) {
      this.impostazioni = JSON.parse(impostazioniSalvate);
    }
  }
}