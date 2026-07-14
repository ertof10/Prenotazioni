export interface AssenzaCollaboratore {
  idAssenzaCollaboratore: number;

  idCollaboratore: number;
  nomeCollaboratore?: string;
  cognomeCollaboratore?: string;

  dataInizio: string;
  dataFine: string;

  oraInizio?: string;
  oraFine?: string;

  tipoAssenza?: string;
  motivo?: string;
  note?: string;

  attivo?: boolean;

  dataCreazione?: string;
  dataModifica?: string;
}