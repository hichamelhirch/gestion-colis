import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../models/colis.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = 'http://localhost:9090/api/clients/clientAuth';

  constructor(private http: HttpClient, private authService: AuthService) { }

  getProfile(): Observable<Client> {
    const token = this.authService.getToken();
    console.log('Auth Token:', token);
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Client>(this.apiUrl, { headers });
  }
}
