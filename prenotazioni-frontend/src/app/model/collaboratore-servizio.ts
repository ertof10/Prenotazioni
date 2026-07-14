export interface CollaboratoreServizio {
  idCollaboratoreServizio: number;

  idCollaboratore: number;
  nomeCollaboratore?: string;
  cognomeCollaboratore?: string;

  idServizio: number;
  nomeServizio?: string;

  prezzo?: number;
  durata?: number;
  note?: string;
  attivo?: boolean;

  dataCreazione?: string;
  dataModifica?: string;
}