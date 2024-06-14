import { Injectable } from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {Colis, StatutColis, TypeLivraison} from "../models/colis.model";


@Injectable({
  providedIn: 'root'
})
export class ColisService {
  private apiUrl = 'http://localhost:9090/api/colis';

  constructor(private http: HttpClient) {
  }



  getColis(): Observable<Colis[]> {
    return this.http.get<Colis[]>(this.apiUrl);
  }

  getColisById(id: number): Observable<Colis> {
    return this.http.get<Colis>(`${this.apiUrl}/${id}`);
  }

  createColis(colis: Colis): Observable<any> {
    return this.http.post(this.apiUrl, colis);
  }

  updateColis(id: number, colis: Colis): Observable<Colis> {
    console.log('Sending update request for Colis:', colis);
    return this.http.put<Colis>(`${this.apiUrl}/${id}`, colis);
  }
  getBarcode(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/barcode`, { responseType: 'blob' });
  }

  deleteColis(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getFrais(colisId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/getFrais/${colisId}`);
  }


  getColisByNumeroColis(numeroColis: string): Observable<Colis> {
    return this.http.get<Colis>(`${this.apiUrl}/search?numeroColis=${numeroColis}`);
  }
  getColisByCodeBarre(codeBarre: string): Observable<Colis> {
    return this.http.get<Colis>(`${this.apiUrl}/searchByCodeBarre`, { params: { codeBarre } });
  }
  createColisEnMasse(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);

    return this.http.post<any>(`${this.apiUrl}/importCSV`, formData);
  }
  updateColisStatusToConfirmed(id: number): Observable<void> {
    console.log(`Calling updateColisStatusToConfirmed for ID: ${id}`); // Log pour vérifier l'appel API
    return this.http.post<void>(`${this.apiUrl}/updateStatus/${id}`, {});
  }

  updateColisStatusToCancel(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/updateStatusToCancel/${id}`, {});
  }
  generateLabel(id: number): Observable<Colis> {
    return this.http.get<Colis>(`${this.apiUrl}/generate-label/${id}`);
  }

  downloadLabel(id: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.apiUrl}/download-label/${id}`, { observe: 'response', responseType: 'blob' });
  }

  removeService(colisId: number, serviceId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${colisId}/removeServices?serviceId=${serviceId}`, {});
  }

  addServiceToColis(colisId: number, serviceId: number): Observable<Colis> {
    return this.http.post<Colis>(`${this.apiUrl}/${colisId}/addService?serviceId=${serviceId}`, {});
  }


  downloadMultipleLabels(colisIds: number[]): Observable<Blob> {
    const params = new HttpParams().set('colisIds', colisIds.join(','));
    return this.http.get(`${this.apiUrl}/download-multiple-labels`, { params, responseType: 'blob' });
  }

  filterColis(statuses: StatutColis[]): Observable<Colis[]> {
    let params = new HttpParams();
    statuses.forEach(status => {
      params = params.append('status', status);
    });
    return this.http.get<Colis[]>(`${this.apiUrl}/filter`, { params });
  }
}
