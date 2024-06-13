import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ClientService } from "../services/client.service";
import { VilleService } from "../services/ville.service";
import { Client, TypeChargeur, Ville } from "../models/colis.model";
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';

@Component({
  selector: 'app-create-clients',
  templateUrl: './create-clients.component.html',
  styleUrls: ['./create-clients.component.css']
})
export class CreateClientsComponent implements OnInit {
  clientForm!: FormGroup;
  villes: Ville[] = [];
  filteredVilles!: Observable<Ville[]>;

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private villeService: VilleService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.clientForm = this.fb.group({
      nomClient: ['', Validators.required],
      prenomClient: ['', Validators.required],
      adresseClient: ['', Validators.required],
      dateNaissance: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      tel: ['', Validators.required],
      villeClient: ['', Validators.required],
      password: ['', Validators.required],
    });

    this.loadVilles();

    this.filteredVilles = this.clientForm.get('villeClient')!.valueChanges
      .pipe(
        startWith(''),
        map(value => this._filterVilles(value))
      );
  }

  loadVilles(): void {
    this.villeService.getVilles().subscribe(villes => {
      this.villes = villes;
    });
  }

  private _filterVilles(value: string): Ville[] {
    const filterValue = value.toLowerCase();
    return this.villes.filter(ville => ville.ville.toLowerCase().includes(filterValue));
  }

  displayVille(ville: Ville): string {
    return ville ? ville.ville : '';
  }

  onSubmit(): void {
    if (this.clientForm.valid) {
      const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
        data: {
          message: 'Êtes-vous sûr de vouloir créer ce client ?'
        }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          const client: Client = this.clientForm.value;
          client.typeChargeur = TypeChargeur.CS;

          this.clientService.createClient(client).subscribe(
            response => {
              console.log('Client créé avec succès', response);
              this.dialog.open(ConfirmationDialogComponent, {
                data: {
                  message: 'Le client a été créé avec succès.'
                }
              });
            },
            error => {
              if (error.status === 409) {
                console.log('Email déjà utilisé');
              } else {
                console.error('Erreur lors de la création du client', error);
              }
            }
          );
        } else {
          console.log('Création de client annulée');
        }
      });
    }
  }
}
