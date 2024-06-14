import { Component, OnInit } from '@angular/core';
import { ProfileService } from '../services/profile.service';
import { Client } from '../models/colis.model';
import { Location } from '@angular/common';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  client!: Client;
  isLoading: boolean = true;

  constructor(private profileService: ProfileService, private location: Location) { }

  ngOnInit(): void {
    this.profileService.getProfile().subscribe(
      data => {
        this.client = data;
        this.isLoading = false;
      },
      error => {
        console.error('Error fetching profile', error);
        this.isLoading = false;
      }
    );
  }

  goBack(): void {
    this.location.back();
  }

  shareProfile(option: string): void {
    const message = `Profil Client:\n\nNom: ${this.client.nomClient}\nTéléphone: ${this.client.tel}\nAdresse: ${this.client.adresseClient}\nVille: ${this.client.villeClient.ville}`;

    switch (option) {
      case 'whatsapp':
        const whatsappUrl = `https://api.whatsapp.com/send?text=${encodeURIComponent(message)}`;
        window.open(whatsappUrl, '_blank');
        break;
      case 'email':
        const mailtoUrl = `mailto:?subject=Profil Client&body=${encodeURIComponent(message)}`;
        window.location.href = mailtoUrl;
        break;
      case 'other':
        if (navigator.share) {
          navigator.share({
            title: 'Profil Client',
            text: message,
          })
            .catch(error => console.error('Error sharing', error));
        } else {
          alert('Le partage via cette méthode n\'est pas supporté par votre navigateur.');
        }
        break;
      default:
        alert('Invalid sharing option.');
    }
  }
}
