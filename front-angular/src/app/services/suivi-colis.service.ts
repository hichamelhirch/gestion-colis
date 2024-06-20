import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import {SuiviColis} from "../models/colis.model";


@Injectable({
  providedIn: 'root'
})
export class SuiviColisService {
  private apiUrl = 'http://localhost:9090/suivi';

  constructor(private http: HttpClient) { }

  getSuiviByColisId(colisId: number): Observable<SuiviColis[]> {
    return this.http.get<SuiviColis[]>(`${this.apiUrl}/${colisId}`);
  }

  ramasserColis(colisId: number, livreurId: number): Observable<SuiviColis> {
    const params = new HttpParams().set('livreurId', livreurId.toString());
    return this.http.post<SuiviColis>(`${this.apiUrl}/${colisId}/ramasser`, null, { params });
  }

  traiterColis(colisId: number, hubId: number): Observable<SuiviColis> {
    const params = new HttpParams().set('hubId', hubId.toString());
    return this.http.post<SuiviColis>(`${this.apiUrl}/${colisId}/traitement`, null, { params });
  }

  livrerColis(colisId: number, livreurId: number): Observable<SuiviColis> {
    const params = new HttpParams().set('livreurId', livreurId.toString());
    return this.http.post<SuiviColis>(`${this.apiUrl}/${colisId}/livrer`, null, { params });
  }

  getOperationsByBarcode(barcode: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/operations/${barcode}`);
  }

  updateDescriptionProbleme(colisId: number, description: string, type: string, id: number): Observable<SuiviColis> {
    const params = new HttpParams().set('description', description).set('type', type).set('id', id.toString());
    return this.http.put<SuiviColis>(`${this.apiUrl}/${colisId}/updateDescriptionProbleme`, null, { params });
  }

  updateSuiviColisStatut(colisId: number, statut: string): Observable<SuiviColis> {
    const params = new HttpParams().set('statut', statut);
    return this.http.put<SuiviColis>(`${this.apiUrl}/${colisId}/updateSuiviColisStatut`, null, { params });
  }
  getColisEnCours(): Observable<SuiviColis[]> {
    return this.http.get<SuiviColis[]>(`${this.apiUrl}/colis-en-cours`);
  }

  getColisLivres(): Observable<SuiviColis[]> {
    return this.http.get<SuiviColis[]>(`${this.apiUrl}/colis-livres`);
  }
}
