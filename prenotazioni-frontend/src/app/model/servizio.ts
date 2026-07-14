export interface Servizio {
  idServizio: number;

  nomeServizio: string;
  categoriaServizio: string;
  descrizioneServizio: string;

  prezzoServizio: number;
  durataServizio?: number;

  urlImmagineServizio?: string;

  attivoServizio?: boolean;

  dataCreazione?: string;
  dataModifica?: string;
}