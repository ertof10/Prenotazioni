export interface Utente {
  idUtente: number;
  nomeUtente: string;
  cognomeUtente: string;
  emailUtente: string;
  telefonoUtente?: string;
  attivoUtente?: boolean;
  dataCreazione?: string;
  dataModifica?: string;
}