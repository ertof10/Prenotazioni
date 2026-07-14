export interface Prenotazione {
  idPrenotazione: number;

  idUtente: number;
  nomeUtente?: string;
  cognomeUtente?: string;
  emailUtente?: string;

  idServizio: number;
  nomeServizio?: string;

  idCollaboratore?: number;
  nomeCollaboratore?: string;
  cognomeCollaboratore?: string;

  dataPrenotazione: string;
  oraInizio: string;
  oraFine: string;

  note?: string;
  statoPrenotazione: string;

  dataCreazione?: string;
  dataModifica?: string;
}