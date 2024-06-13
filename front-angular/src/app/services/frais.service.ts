// frais.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TypeLivraison } from '../models/colis.model';

@Injectable({
  providedIn: 'root'
})
export class FraisService {

  constructor(private http: HttpClient) {}

  calculateFrais(typeLivraison: TypeLivraison, villeDepartId: number, villeDestinataireId: number): Observable<number> {
    const url = `http://localhost:9090/api/villes/getFraisFromCamunda?typeLivraison=${typeLivraison}&villeDepartId=${villeDepartId}&villeDestinataireId=${villeDestinataireId}`;
    return this.http.get<number>(url);
  }
}

