export interface Preventivo {
  idPreventivo: number;

  idUtente: number;
  nomeUtente?: string;
  cognomeUtente?: string;
  emailUtente?: string;

  idServizio?: number;
  nomeServizio?: string;

  descrizioneRichiesta?: string;
  descrizionePreventivo?: string;

  importoPreventivo?: number;
  statoPreventivo: string;

  dataRichiesta?: string;
  dataInvio?: string;
  dataAccettazione?: string;
  dataRifiuto?: string;

  dataCreazione?: string;
  dataModifica?: string;
}