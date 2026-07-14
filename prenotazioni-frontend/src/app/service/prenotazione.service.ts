import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Prenotazione } from '../model/prenotazione';
import { PageResponse } from '../model/page-response';

@Injectable({
  providedIn: 'root'
})
export class PrenotazioneService {

  private apiUrl = 'http://localhost:8080/prenotazione';

  constructor(private http: HttpClient) { }

  stampaTutti(): Observable<PageResponse<Prenotazione>> {
    return this.http.get<PageResponse<Prenotazione>>(`${this.apiUrl}/stampa-tutti`);
  }

  stampaPerUtente(idUtente: number): Observable<PageResponse<Prenotazione>> {
    return this.http.get<PageResponse<Prenotazione>>(`${this.apiUrl}/stampa-per-utente/${idUtente}`);
  }

  stampaDaRiprogrammare(): Observable<PageResponse<Prenotazione>> {
    return this.http.get<PageResponse<Prenotazione>>(`${this.apiUrl}/stampa-da-riprogrammare`);
  }

  stampaDaRiprogrammarePerUtente(idUtente: number): Observable<PageResponse<Prenotazione>> {
    return this.http.get<PageResponse<Prenotazione>>(
      `${this.apiUrl}/stampa-da-riprogrammare-per-utente/${idUtente}`
    );
  }

  cercaPerId(idPrenotazione: number): Observable<Prenotazione> {
    return this.http.get<Prenotazione>(`${this.apiUrl}/cerca-per-id/${idPrenotazione}`);
  }

  utenteAnnullaPrenotazione(idPrenotazione: number): Observable<any> {
    return this.http.put<any>(
      `${this.apiUrl}/utente-annulla-prenotazione/${idPrenotazione}`,
      {}
    );
  }

  strutturaAnnullaPrenotazione(idPrenotazione: number): Observable<any> {
    return this.http.put<any>(
      `${this.apiUrl}/struttura-annulla-prenotazione/${idPrenotazione}`,
      {}
    );
  }
}