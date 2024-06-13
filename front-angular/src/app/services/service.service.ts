import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ServiceALivraison} from "../models/colis.model";

@Injectable({
  providedIn: 'root'
})
export class ServiceService {

  private apiUrl = 'http://localhost:9090/api/services';

  constructor(private http: HttpClient) {}

  getServices(): Observable<ServiceALivraison[]> {
    return this.http.get<ServiceALivraison[]>(this.apiUrl);
  }
  getServiceById(id: number): Observable<ServiceALivraison> {
    return this.http.get<ServiceALivraison>(`${this.apiUrl}/${id}`);
  }
}
