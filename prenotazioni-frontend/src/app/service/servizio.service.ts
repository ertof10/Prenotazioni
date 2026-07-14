import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Servizio } from '../model/servizio';
import { PageResponse } from '../model/page-response';

@Injectable({
  providedIn: 'root'
})
export class ServizioService {

  private apiUrl = 'http://localhost:8080/servizio';

  constructor(private http: HttpClient) { }

  stampaAttivi(): Observable<PageResponse<Servizio>> {
    return this.http.get<PageResponse<Servizio>>(`${this.apiUrl}/stampa-attivi`);
  }

  stampaTutti(): Observable<PageResponse<Servizio>> {
    return this.http.get<PageResponse<Servizio>>(`${this.apiUrl}/stampa-tutti`);
  }

  cercaPerId(idServizio: number): Observable<Servizio> {
    return this.http.get<Servizio>(`${this.apiUrl}/cerca-per-id/${idServizio}`);
  }
}