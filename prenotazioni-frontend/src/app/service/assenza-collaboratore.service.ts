import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AssenzaCollaboratore } from '../model/assenza-collaboratore';
import { PageResponse } from '../model/page-response';

@Injectable({
  providedIn: 'root'
})
export class AssenzaCollaboratoreService {

  private apiUrl = 'http://localhost:8080/assenze-collaboratori';

  constructor(private http: HttpClient) { }

  stampaTutti(): Observable<PageResponse<AssenzaCollaboratore>> {
    return this.http.get<PageResponse<AssenzaCollaboratore>>(`${this.apiUrl}/stampa-tutti`);
  }

  stampaPerCollaboratore(idCollaboratore: number): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/stampa-per-collaboratore/${idCollaboratore}`
    );
  }

  cercaPerId(idAssenzaCollaboratore: number): Observable<AssenzaCollaboratore> {
    return this.http.get<AssenzaCollaboratore>(
      `${this.apiUrl}/cerca-per-id/${idAssenzaCollaboratore}`
    );
  }
}