import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Utente } from '../model/utente';
import { PageResponse } from '../model/page-response';

@Injectable({
  providedIn: 'root'
})
export class UtenteService {

  private apiUrl = 'http://localhost:8080/utente';

  constructor(private http: HttpClient) { }

  stampaTutti(): Observable<PageResponse<Utente>> {
    return this.http.get<PageResponse<Utente>>(`${this.apiUrl}/stampa-tutti`);
  }

  cercaPerId(idUtente: number): Observable<Utente> {
    return this.http.get<Utente>(`${this.apiUrl}/cerca-per-id/${idUtente}`);
  }
}