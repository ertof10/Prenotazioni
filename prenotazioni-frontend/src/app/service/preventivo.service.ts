import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Preventivo } from '../model/preventivo';
import { PageResponse } from '../model/page-response';

@Injectable({
  providedIn: 'root'
})
export class PreventivoService {

  private apiUrl = 'http://localhost:8080/preventivo';

  constructor(private http: HttpClient) { }

  stampaTutti(): Observable<PageResponse<Preventivo>> {
    return this.http.get<PageResponse<Preventivo>>(`${this.apiUrl}/stampa-tutti`);
  }

  stampaPerUtente(idUtente: number): Observable<PageResponse<Preventivo>> {
    return this.http.get<PageResponse<Preventivo>>(`${this.apiUrl}/stampa-per-utente/${idUtente}`);
  }

  stampaPerStato(statoPreventivo: string): Observable<PageResponse<Preventivo>> {
    return this.http.get<PageResponse<Preventivo>>(`${this.apiUrl}/stampa-per-stato/${statoPreventivo}`);
  }

  cercaPerId(idPreventivo: number): Observable<Preventivo> {
    return this.http.get<Preventivo>(`${this.apiUrl}/cerca-per-id/${idPreventivo}`);
  }

  accetta(idPreventivo: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/accetta/${idPreventivo}`, {});
  }

  rifiuta(idPreventivo: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/rifiuta/${idPreventivo}`, {});
  }

  prendiInLavorazione(idPreventivo: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/prendi-in-lavorazione/${idPreventivo}`, {});
  }

  invia(idPreventivo: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/invia/${idPreventivo}`, {});
  }

  chiudiManualmente(idPreventivo: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/chiudi-manualmente/${idPreventivo}`, {});
  }
}