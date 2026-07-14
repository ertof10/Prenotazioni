import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Collaboratore } from '../model/collaboratore';
import { PageResponse } from '../model/page-response';

@Injectable({
  providedIn: 'root'
})
export class CollaboratoreService {

  private apiUrl = 'http://localhost:8080/collaboratore';

  constructor(private http: HttpClient) { }

  stampaTutti(): Observable<PageResponse<Collaboratore>> {
    return this.http.get<PageResponse<Collaboratore>>(`${this.apiUrl}/stampa-tutti`);
  }

  stampaAttivi(): Observable<PageResponse<Collaboratore>> {
    return this.http.get<PageResponse<Collaboratore>>(`${this.apiUrl}/stampa-attivi`);
  }

  cercaPerId(idCollaboratore: number): Observable<Collaboratore> {
    return this.http.get<Collaboratore>(`${this.apiUrl}/cerca-per-id/${idCollaboratore}`);
  }
}