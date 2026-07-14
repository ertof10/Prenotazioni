export interface Collaboratore {
  idCollaboratore: number;

  nomeCollaboratore: string;
  cognomeCollaboratore: string;
  emailCollaboratore: string;

  telefonoCollaboratore?: string;
  specializzazioneCollaboratore?: string;
  descrizioneCollaboratore?: string;

  professioneCollaboratore?: string;
  urlFotoCollaboratore?: string;

  attivoCollaboratore?: boolean;

  dataCreazione?: string;
  dataModifica?: string;
}