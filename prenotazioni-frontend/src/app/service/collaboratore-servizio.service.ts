import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CollaboratoreServizio } from '../model/collaboratore-servizio';
import { PageResponse } from '../model/page-response';

@Injectable({
  providedIn: 'root'
})
export class CollaboratoreServizioService {

  private apiUrl = 'http://localhost:8080/collaboratore-servizio';

  constructor(private http: HttpClient) { }

  stampaTutti(): Observable<PageResponse<CollaboratoreServizio>> {
    return this.http.get<PageResponse<CollaboratoreServizio>>(`${this.apiUrl}/stampa-tutti`);
  }

  cercaPerId(idCollaboratoreServizio: number): Observable<CollaboratoreServizio> {
    return this.http.get<CollaboratoreServizio>(
      `${this.apiUrl}/cerca-per-id/${idCollaboratoreServizio}`
    );
  }

  stampaServiziPerCollaboratore(idCollaboratore: number): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/stampa-servizi-per-collaboratore/${idCollaboratore}`
    );
  }

  stampaCollaboratoriPerServizio(idServizio: number): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/stampa-collaboratori-per-servizio/${idServizio}`
    );
  }
}