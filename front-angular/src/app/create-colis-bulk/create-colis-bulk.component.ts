import { Component } from '@angular/core';
import { ColisService } from '../services/colis.service';
import { VilleService } from '../services/ville.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-colis-bulk',
  templateUrl: './create-colis-bulk.component.html',
  styleUrls: ['./create-colis-bulk.component.css']
})
export class CreateColisBulkComponent {
  selectedFile: File | null = null;
  errorMessages: string[] = [];
  logMessages: string[] = [];
  similarVilles: { [key: string]: any[] } = {};
  colisCount: number = 0;
  colisSaved: number = 0;
  colisRejected: number = 0;
  responseMessage: string = '';
  showForm: boolean = true;

  constructor(private colisService: ColisService, private villeService: VilleService, private router: Router) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.selectedFile) {
      this.colisService.createColisEnMasse(this.selectedFile).subscribe(
        response => {
          console.log('Response received:', response);
          this.showForm = false;
          if (response.success) {
            this.errorMessages = response.errorMessages || [];
            this.logMessages = response.logMessages || [];
            this.colisCount = response.colisCount || 0;
            this.colisSaved = this.colisCount;
            this.colisRejected = response.rejectedCount || this.errorMessages.length;

            this.errorMessages.forEach(error => {
              const villeNameMatch = error.match(/\((.*?)\)/);
              if (villeNameMatch && villeNameMatch[1]) {
                const villeName = villeNameMatch[1];
                console.log(`Fetching similar villes for: ${villeName}`);
                this.villeService.getSimilarVilles(villeName).subscribe(
                  villes => {
                    console.log(`Similar villes for ${villeName}:`, villes);
                    this.similarVilles[villeName] = villes;
                    console.log('Updated similarVilles:', this.similarVilles);
                  },
                  (err: HttpErrorResponse) => console.error('Error fetching similar cities:', err)
                );
              }
            });

            console.log('Colis saved:', this.colisSaved);
            console.log('Colis rejected:', this.colisRejected);
          } else {
            this.responseMessage = response.message;
          }
        },
        (error: HttpErrorResponse) => {
          console.error('Import failed:', error);
          this.responseMessage = 'Import failed: ' + (error.error.message || error.message);
          this.showForm = false;
        }
      );
    } else {
      console.warn('No file selected');
      alert('Please select a file.');
    }
  }

  downloadTemplate(): void {
    const url = '/assets/colis_template.csv';
    const a = document.createElement('a');
    a.href = url;
    a.download = 'colis_template.csv';
    a.click();
  }

  resetForm(): void {
    this.selectedFile = null;
    this.errorMessages = [];
    this.logMessages = [];
    this.similarVilles = {};
    this.colisCount = 0;
    this.colisSaved = 0;
    this.colisRejected = 0;
    this.responseMessage = '';
    this.showForm = true;
  }

  getVilleNameFromMessage(message: string): string {
    const villeNameMatch = message.match(/\((.*?)\)/);
    return villeNameMatch ? villeNameMatch[1] : '';
  }

  getSimilarVillesForMessage(message: string): any[] {
    const villeName = this.getVilleNameFromMessage(message);
    console.log(`Getting similar villes for message: ${message}, villeName: ${villeName}`);
    return this.similarVilles[villeName] || [];
  }
}
