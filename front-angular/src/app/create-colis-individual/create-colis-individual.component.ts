import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { VilleService } from '../services/ville.service';
import { ColisService } from '../services/colis.service';
import { FraisService } from '../services/frais.service';
import { Colis, ServiceALivraison, StatutColis, TypeLivraison, Ville } from '../models/colis.model';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { Observable } from "rxjs";
import { map, startWith } from "rxjs/operators";
import { Router } from "@angular/router";
import { ServiceService } from "../services/service.service";
import {SuccessDialogComponent} from "../success-dialog/success-dialog.component";

@Component({
  selector: 'app-create-colis-individual',
  templateUrl: './create-colis-individual.component.html',
  styleUrls: ['./create-colis-individual.component.css']
})
export class CreateColisIndividualComponent implements OnInit {
  colisForm!: FormGroup;
  villes: Ville[] = [];
  currentStep: number = 1;
  displayedColumns: string[] = ['select', 'serviceName', 'description', 'price', 'status'];
  services: ServiceALivraison[] = [];
  selectedServices: ServiceALivraison[] = [];

  filteredVillesClientDestinataire!: Observable<Ville[]>;
  filteredVillesClientDepart!: Observable<Ville[]>;

  statutColisOptions = Object.values(StatutColis);
  typeLivraisonOptions = Object.values(TypeLivraison);
  frais: number = 0;

  constructor(
    private fb: FormBuilder,
    private villeService: VilleService,
    private colisService: ColisService,
    private fraisService: FraisService,
    public dialog: MatDialog,
    private router: Router,
    private serviceService: ServiceService,
  ) {}

  ngOnInit(): void {
    this.initializeForms();
    this.loadVilles();
    this.loadServices();

    this.filteredVillesClientDestinataire = this.colisForm.get('villeDestinataire')!.valueChanges.pipe(
      startWith(''),
      map(value => this._filterVilles(value))
    );

    this.filteredVillesClientDepart = this.colisForm.get('villeDepart')!.valueChanges.pipe(
      startWith(''),
      map(value => this._filterVilles(value))
    );
  }

  initializeForms(): void {
    this.colisForm = this.fb.group({
      typeLivraison: ['', Validators.required],
      cashDelivery: ['', [Validators.required, Validators.min(0)]],
      poids: ['', [Validators.required, Validators.min(0)]],
      frais: [{ value: 0, disabled: true }, Validators.required],
      longueur: ['', [Validators.required, Validators.min(0)]],
      largeur: ['', [Validators.required, Validators.min(0)]],
      hauteur: ['', [Validators.required, Validators.min(0)]],
      nomDestinataire: ['', Validators.required],
      telDestinataire: ['', Validators.required],
      adresseDestinataire: ['', Validators.required],
      villeDestinataire: ['', Validators.required],
      description: [''],
      adresse2Destinataire: [''],
      valeurColis: [''],
      villeDepart: ['', Validators.required],
      numTelChargeur: ['', Validators.required],
      premiereAdresseChargeur: ['', Validators.required],
      deuxiemeAdresseChargeur: ['', Validators.required]
    });
  }

  loadServices(): void {
    this.serviceService.getServices().subscribe(services => {
      this.services = services;
    });
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

  calculateFrais(): void {
    const typeLivraison = this.colisForm.get('typeLivraison')?.value;
    const villeDepart = this.colisForm.get('villeDepart')?.value;
    const villeDestinataire = this.colisForm.get('villeDestinataire')?.value;

    if (typeLivraison && villeDepart && villeDestinataire) {
      this.fraisService.calculateFrais(typeLivraison, villeDepart.id, villeDestinataire.id).subscribe(
        (frais: number) => {
          /*  this.frais = frais;
            this.fraisControl.setValue(frais);


           */
          const fraisServices = this.selectedServices.reduce((total, service) => total + service.price, 0);
          const totalFrais = frais + fraisServices;
          this.frais = totalFrais;
          this.fraisControl.setValue(totalFrais);
        },
        (error: any) => {
          console.error('Erreur lors du calcul des frais', error);
        }
      );
    }
  }

  nextStep(): void {
    if (this.isCurrentStepValid()) {
      if (this.currentStep === 2) {
        this.calculateFrais();
      }
      this.currentStep++;
    } else {
      alert('Veuillez remplir tous les champs obligatoires avant de continuer.');
    }
  }

  previousStep(): void {
    this.currentStep--;
  }

  isCurrentStepValid(): boolean {
    if (this.currentStep === 1) {
      return this.colisForm.controls['typeLivraison'].valid;
    } else if (this.currentStep === 2) {
      return (
        this.colisForm.controls['nomDestinataire'].valid &&
        this.colisForm.controls['telDestinataire'].valid &&
        this.colisForm.controls['adresseDestinataire'].valid &&
        this.colisForm.controls['villeDestinataire'].valid &&
        this.colisForm.controls['villeDepart'].valid &&
        this.colisForm.controls['numTelChargeur'].valid &&
        this.colisForm.controls['premiereAdresseChargeur'].valid &&
        this.colisForm.controls['deuxiemeAdresseChargeur'].valid
      );
    } else if (this.currentStep === 3) {
      return (
        this.colisForm.controls['cashDelivery'].valid &&
        this.colisForm.controls['poids'].valid &&
        this.colisForm.controls['longueur'].valid &&
        this.colisForm.controls['largeur'].valid &&
        this.colisForm.controls['hauteur'].valid
      );
    }
    return false;
  }

  /* onServiceSelect(service: ServiceALivraison, isChecked: boolean): void {
     if (isChecked) {
       this.selectedServices.push(service);
     } else {
       const index = this.selectedServices.findIndex(s => s.id === service.id);
       this.selectedServices.splice(index, 1);
     }
   }



   */
  onServiceSelect(service: ServiceALivraison, isChecked: boolean): void {
    if (isChecked) {
      this.selectedServices.push(service);
    } else {
      const index = this.selectedServices.findIndex(s => s.id === service.id);
      this.selectedServices.splice(index, 1);
    }
    this.calculateFrais();
  }




  onSubmit(): void {
    if (this.colisForm.valid) {
      const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
        data: {
          message: 'Êtes-vous sûr de vouloir créer ce colis ?'
        }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          const colis: Colis = this.colisForm.value;
          colis.creationDate = new Date();
          colis.statusColis = StatutColis.BROUILLON;
          colis.frais = this.frais;

          colis.services = this.selectedServices;

          this.createColis(colis);
        }
      });
    } else {
      console.error('Form is invalid');
    }
  }

  createColis(colis: Colis): void {
    this.colisService.createColis(colis).subscribe(
      createdColis => {
        console.log('Colis créé avec succès', createdColis);
        const successDialogRef = this.dialog.open(SuccessDialogComponent);
        successDialogRef.afterClosed().subscribe(() => {
          this.router.navigate(['/home/colislist']);
        });
      },
      (error: any) => {
        console.error('Erreur lors de la création du colis', error);
      }
    );
  }

  get fraisControl(): FormControl {
    return this.colisForm.get('frais') as FormControl;
  }
}
