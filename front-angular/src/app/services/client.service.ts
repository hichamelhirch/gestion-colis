import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Client, Colis} from "../models/colis.model";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private apiUrl = 'http://localhost:9090/api/clients';

  constructor(private http: HttpClient) { }

  createClient(client: Client): Observable<Client> {
    return this.http.post<Client>(this.apiUrl, client);
  }
  getClientById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
  }
  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  getClientByUserId(userId: number): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/user/${userId}`);
  }
}
