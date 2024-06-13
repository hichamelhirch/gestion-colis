import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ClientService } from '../services/client.service';
import { Client } from '../models/colis.model';
import jwt_decode from 'jwt-decode'; // Importation par défaut correcte

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent  {
 /* client: Client | null = null;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private clientService: ClientService
  ) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (token) {
      const decodedToken: any = this.decodeToken(token);
      console.log('Decoded Token:', decodedToken); // Log le token décodé pour vérifier les valeurs
      const userId = decodedToken ? decodedToken.userId : null; // Assurez-vous que userId est extrait correctement
      console.log('User ID:', userId); // Log userId pour s'assurer qu'il est présent
      if (userId) {
        this.clientService.getClientByUserId(userId).subscribe(
          (data: Client) => {
            this.client = data;
          },
          error => {
            this.errorMessage = 'Erreur lors du chargement des informations du client';
            console.error('Failed to load client data', error);
          }
        );
      } else {
        this.errorMessage = 'ID utilisateur non trouvé dans le token';
      }
    }
  }

  private decodeToken(token: string): any {
    try {
      // @ts-ignore
      return jwt_decode(token); // Utilisation correcte de jwt-decode
    } catch (error) {
      console.error('Erreur lors du décodage du token', error);
      return null;
    }
  }

  */
}
