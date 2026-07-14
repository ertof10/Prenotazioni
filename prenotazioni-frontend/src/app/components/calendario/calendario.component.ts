import { Component, OnInit } from '@angular/core';

interface GiornoCalendario {
  data: Date;
  numeroGiorno: number;
  meseCorrente: boolean;
  oggi: boolean;
  selezionato: boolean;
  numeroAppuntamenti: number;
  slotLiberi: number;
  slotOccupati: number;
  assenza: boolean;
}

@Component({
  selector: 'app-calendario',
  standalone: false,
  templateUrl: './calendario.component.html',
  styleUrl: './calendario.component.css'
})
export class CalendarioComponent implements OnInit {

  dataCorrente: Date = new Date();
  dataSelezionata: Date = new Date();
  giornoSelezionato?: GiornoCalendario;

  settimane: GiornoCalendario[][] = [];

  nomiGiorni: string[] = [
    'Lun',
    'Mar',
    'Mer',
    'Gio',
    'Ven',
    'Sab',
    'Dom'
  ];

  nomiMesi: string[] = [
    'Gennaio',
    'Febbraio',
    'Marzo',
    'Aprile',
    'Maggio',
    'Giugno',
    'Luglio',
    'Agosto',
    'Settembre',
    'Ottobre',
    'Novembre',
    'Dicembre'
  ];

  ngOnInit(): void {
    this.generaCalendario();
  }

  get meseAnnoCorrente(): string {
    const mese = this.nomiMesi[this.dataCorrente.getMonth()];
    const anno = this.dataCorrente.getFullYear();

    return `${mese} ${anno}`;
  }

  get totaleAppuntamentiMese(): number {
    return this.settimane
      .reduce((giorni, settimana) => giorni.concat(settimana), [])
      .filter(giorno => giorno.meseCorrente)
      .reduce((totale, giorno) => totale + giorno.numeroAppuntamenti, 0);
  }

  get totaleSlotLiberiMese(): number {
    return this.settimane
      .reduce((giorni, settimana) => giorni.concat(settimana), [])
      .filter(giorno => giorno.meseCorrente)
      .reduce((totale, giorno) => totale + giorno.slotLiberi, 0);
  }

  get totaleSlotOccupatiMese(): number {
    return this.settimane
      .reduce((giorni, settimana) => giorni.concat(settimana), [])
      .filter(giorno => giorno.meseCorrente)
      .reduce((totale, giorno) => totale + giorno.slotOccupati, 0);
  }

  mesePrecedente(): void {
    this.dataCorrente = new Date(
      this.dataCorrente.getFullYear(),
      this.dataCorrente.getMonth() - 1,
      1
    );

    this.generaCalendario();
  }

  meseSuccessivo(): void {
    this.dataCorrente = new Date(
      this.dataCorrente.getFullYear(),
      this.dataCorrente.getMonth() + 1,
      1
    );

    this.generaCalendario();
  }

  tornaAlMeseCorrente(): void {
    this.dataCorrente = new Date();
    this.dataSelezionata = new Date();

    this.generaCalendario();
  }

  selezionaGiorno(giorno: GiornoCalendario): void {
    this.dataSelezionata = giorno.data;
    this.giornoSelezionato = giorno;

    this.aggiornaGiornoSelezionato();
  }

  formattaData(data: Date): string {
    return data.toLocaleDateString('it-IT', {
      weekday: 'long',
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    });
  }

  private generaCalendario(): void {

    const anno = this.dataCorrente.getFullYear();
    const mese = this.dataCorrente.getMonth();

    const primoGiornoDelMese = new Date(anno, mese, 1);
    const giornoSettimanaPrimo = this.convertiGiornoSettimana(primoGiornoDelMese.getDay());

    const dataInizioCalendario = new Date(primoGiornoDelMese);
    dataInizioCalendario.setDate(primoGiornoDelMese.getDate() - giornoSettimanaPrimo);

    const settimaneGenerate: GiornoCalendario[][] = [];

    let dataDaInserire = new Date(dataInizioCalendario);

    for (let settimanaIndex = 0; settimanaIndex < 6; settimanaIndex++) {

      const settimana: GiornoCalendario[] = [];

      for (let giornoIndex = 0; giornoIndex < 7; giornoIndex++) {

        const dataGiorno = new Date(dataDaInserire);
        const meseCorrente = dataGiorno.getMonth() === mese;

        const giornoCalendario: GiornoCalendario = {
          data: dataGiorno,
          numeroGiorno: dataGiorno.getDate(),
          meseCorrente: meseCorrente,
          oggi: this.isStessaData(dataGiorno, new Date()),
          selezionato: this.isStessaData(dataGiorno, this.dataSelezionata),
          numeroAppuntamenti: meseCorrente ? this.calcolaNumeroAppuntamentiDemo(dataGiorno) : 0,
          slotLiberi: meseCorrente ? this.calcolaSlotLiberiDemo(dataGiorno) : 0,
          slotOccupati: meseCorrente ? this.calcolaSlotOccupatiDemo(dataGiorno) : 0,
          assenza: meseCorrente ? this.calcolaAssenzaDemo(dataGiorno) : false
        };

        settimana.push(giornoCalendario);

        dataDaInserire.setDate(dataDaInserire.getDate() + 1);
      }

      settimaneGenerate.push(settimana);
    }

    this.settimane = settimaneGenerate;
    this.aggiornaGiornoSelezionato();
  }

  private aggiornaGiornoSelezionato(): void {

    this.settimane = this.settimane.map(settimana =>
      settimana.map(giorno => ({
        ...giorno,
        selezionato: this.isStessaData(giorno.data, this.dataSelezionata)
      }))
    );

    const tuttiGiorni = this.settimane.reduce(
      (giorni, settimana) => giorni.concat(settimana),
      []
    );

    this.giornoSelezionato = tuttiGiorni.find(
      giorno => this.isStessaData(giorno.data, this.dataSelezionata)
    );
  }

  private convertiGiornoSettimana(giornoJavascript: number): number {

    if (giornoJavascript === 0) {
      return 6;
    }

    return giornoJavascript - 1;
  }

  private isStessaData(dataA: Date, dataB: Date): boolean {
    return dataA.getFullYear() === dataB.getFullYear()
      && dataA.getMonth() === dataB.getMonth()
      && dataA.getDate() === dataB.getDate();
  }

  private calcolaNumeroAppuntamentiDemo(data: Date): number {
    const giorno = data.getDate();

    if (giorno % 9 === 0) {
      return 0;
    }

    if (giorno % 7 === 0) {
      return 5;
    }

    if (giorno % 5 === 0) {
      return 3;
    }

    if (giorno % 3 === 0) {
      return 2;
    }

    return 1;
  }

  private calcolaSlotLiberiDemo(data: Date): number {
    const giorno = data.getDate();

    if (giorno % 9 === 0) {
      return 0;
    }

    if (giorno % 7 === 0) {
      return 2;
    }

    if (giorno % 5 === 0) {
      return 4;
    }

    return 6;
  }

  private calcolaSlotOccupatiDemo(data: Date): number {
    return this.calcolaNumeroAppuntamentiDemo(data);
  }

  private calcolaAssenzaDemo(data: Date): boolean {
    return data.getDate() % 13 === 0;
  }
}