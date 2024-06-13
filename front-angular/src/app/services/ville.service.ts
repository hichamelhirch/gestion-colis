import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {TypeLivraison, Ville} from "../models/colis.model";

@Injectable({
  providedIn: 'root'
})
export class VilleService {

  private apiUrl = 'http://localhost:9090/api/villes';

  constructor(private http: HttpClient) { }

  getVilles(): Observable<Ville[]> {
    return this.http.get<Ville[]>(this.apiUrl);
  }
  getFraisFromCamunda(typeLivraison: TypeLivraison, villeDepart: number, villeDestinataire: number): Observable<number> {
    const params = new HttpParams()
      .set('typeLivraison', typeLivraison)
      .set('villeDepartId', villeDepart.toString())
      .set('villeDestinataireId', villeDestinataire.toString());

    return this.http.get<number>(`${this.apiUrl}/getFraisFromCamunda`, { params });
  }
  getSimilarVilles(villeName: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/similar`, { params: { villeName } });
  }

  getVilleById(id: number): Observable<Ville> {
    return this.http.get<Ville>(`${this.apiUrl}/${id}`);
  }
}
