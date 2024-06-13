import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ColisService } from '../services/colis.service';
import { VilleService } from '../services/ville.service';
import { ServiceService } from '../services/service.service';
import { Colis, Ville, ServiceALivraison } from '../models/colis.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-edit-colis',
  templateUrl: './edit-colis.component.html',
  styleUrls: ['./edit-colis.component.css']
})
export class EditColisComponent implements OnInit {
  editForm: FormGroup;
  villes: Ville[] = [];
  services: ServiceALivraison[] = [];
  filteredVillesDestinataire!: Observable<Ville[]>;
  filteredVillesDepart!: Observable<Ville[]>;
  filteredServices: Observable<ServiceALivraison[]>[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private colisService: ColisService,
    private villeService: VilleService,
    private serviceService: ServiceService,
    private snackBar: MatSnackBar
  ) {
    this.editForm = this.fb.group({
      typeLivraison: ['', Validators.required],
      cashDelivery: ['', Validators.required],
      poids: ['', Validators.required],
      longueur: ['', Validators.required],
      largeur: ['', Validators.required],
      hauteur: ['', Validators.required],
      nomDestinataire: ['', Validators.required],
      telDestinataire: ['', Validators.required],
      adresseDestinataire: ['', Validators.required],
      adresse2Destinataire: [''],
      description: [''],
      valeurColis: [''],
      villeDestinataire: ['', Validators.required],
      villeDepart: ['', Validators.required],
      numTelChargeur: ['', Validators.required],
      premiereAdresseChargeur: ['', Validators.required],
      deuxiemeAdresseChargeur: [''],
      services: this.fb.array([]),
      frais: [{ value: '', disabled: true }]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.colisService.getColisById(+id).subscribe((colis: Colis) => {
        console.log('Colis récupéré:', colis);
        this.villeService.getVilleById(colis.villeDestinataire.id).subscribe((villeDest: Ville) => {
          this.editForm.patchValue({
            villeDestinataire: villeDest
          });
        });
        this.villeService.getVilleById(colis.villeDepart.id).subscribe((villeDep: Ville) => {
          this.editForm.patchValue({
            villeDepart: villeDep
          });
        });
        this.editForm.patchValue({
          ...colis
        });
        this.setServices(colis.services);
      });
    }

    this.villeService.getVilles().subscribe((villes: Ville[]) => {
      console.log('Villes:', villes);
      this.villes = villes;

      this.filteredVillesDestinataire = this.editForm.get('villeDestinataire')!.valueChanges.pipe(
        startWith(''),
        map(value => this._filterVilles(value))
      );

      this.filteredVillesDepart = this.editForm.get('villeDepart')!.valueChanges.pipe(
        startWith(''),
        map(value => this._filterVilles(value))
      );
    });

    this.serviceService.getServices().subscribe((services: ServiceALivraison[]) => {
      console.log('Services:', services);
      this.services = services;
    });

    this.editForm.valueChanges.pipe(
      map(formValue => ({
        typeLivraison: formValue.typeLivraison,
        villeDepart: formValue.villeDepart.id,
        villeDestinataire: formValue.villeDestinataire.id
      })),
      switchMap(params => this.villeService.getFraisFromCamunda(params.typeLivraison, params.villeDepart, params.villeDestinataire))
    ).subscribe(frais => {
      console.log('Frais récupérés:', frais);
      this.editForm.patchValue({ frais }, { emitEvent: false });
    });
  }

  private _filterVilles(value: any): Ville[] {
    console.log('Filtrage des villes avec:', value);
    if (!value) {
      return this.villes;
    }

    let filterValue: string;

    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else if (value && value.ville) {
      filterValue = value.ville.toLowerCase();
    } else {
      return this.villes;
    }

    return this.villes.filter(ville => ville.ville.toLowerCase().includes(filterValue));
  }

  displayVille(ville: Ville): string {
    return ville && ville.ville ? ville.ville : '';
  }

  private _filterServices(value: string): ServiceALivraison[] {
    console.log('Filtrage des services avec:', value);
    const filterValue = value.toLowerCase();
    return this.services.filter(service => service.serviceName.toLowerCase().includes(filterValue));
  }

  displayServiceName(service: ServiceALivraison): string {
    return service && service.serviceName ? service.serviceName : '';
  }

  get servicesFormArray(): FormArray {
    return this.editForm.get('services') as FormArray;
  }

  addService(): void {
    const serviceFormGroup = this.fb.group({
      id: ['', Validators.required],
      serviceName: ['', Validators.required],
      description: [''],
      price: [0],
      status: ['']
    });
    this.servicesFormArray.push(serviceFormGroup);
    this.initializeServiceControl(serviceFormGroup, this.servicesFormArray.length - 1);
    console.log("Service row added:", serviceFormGroup);
    console.log("FormArray:", this.servicesFormArray);
    console.log("Form status after adding service:", this.editForm.status);
  }

  removeService(index: number): void {
    const serviceGroup = this.servicesFormArray.at(index) as FormGroup;
    const serviceId = serviceGroup.get('id')!.value;

    this.servicesFormArray.removeAt(index);
    this.editForm.updateValueAndValidity();

    const colisId = this.route.snapshot.paramMap.get('id');
    if (colisId) {
      this.colisService.removeService(+colisId, serviceId).subscribe(
        () => {
          this.snackBar.open('Service supprimé avec succès!', 'Fermer', {
            duration: 2000,
          });
          this.colisService.getColisById(+colisId).subscribe((colis: Colis) => {
            this.setServices(colis.services);
          });
        },
        error => {
          console.error('Erreur lors de la suppression du service:', error);
        }
      );
    }
  }

  initializeServiceControl(serviceFormGroup: FormGroup, index: number): void {
    const serviceNameControl = serviceFormGroup.get('serviceName') as FormControl;
    this.filteredServices[index] = serviceNameControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filterServices(value))
    );

    serviceNameControl.valueChanges.subscribe(value => {
      const selectedService = this.services.find(service => service.id === value);
      if (selectedService) {
        serviceFormGroup.patchValue({
          description: selectedService.description,
          price: selectedService.price,
          status: selectedService.status
        });
      }
    });
  }

  setServices(services: ServiceALivraison[]): void {
    console.log('Setting services:', services);
    const serviceFormGroups = services.map(service => this.fb.group({
      id: [service.id, Validators.required],
      serviceName: [service.serviceName, Validators.required],
      description: [service.description],
      price: [service.price, Validators.required],
      status: [service.status]
    }));
    const serviceFormArray = this.fb.array(serviceFormGroups);
    this.editForm.setControl('services', serviceFormArray);

    serviceFormGroups.forEach((group, index) => {
      this.initializeServiceControl(group, index);
    });
  }

  updateServiceDetails(index: number, serviceId: number): void {
    const selectedService = this.services.find(service => service.id === serviceId);
    if (selectedService) {
      const serviceGroup = this.servicesFormArray.at(index) as FormGroup;
      serviceGroup.patchValue({
        description: selectedService.description,
        price: selectedService.price,
        status: selectedService.status
      });
    }
  }

  onSubmit(): void {
    if (this.editForm.valid) {
      const formValue = this.editForm.value;
      const updatedColis: Colis = {
        ...formValue,
        villeDestinataire: { id: formValue.villeDestinataire.id },
        villeDepart: { id: formValue.villeDepart.id },
        services: formValue.services.map((service: any) => ({
          id: service.id,
          serviceName: service.serviceName,
          description: service.description,
          price: service.price,
          status: service.status
        }))
      };

      console.log('Updated Colis:', updatedColis);

      const id = this.route.snapshot.paramMap.get('id');
      if (id) {
        this.colisService.updateColis(+id, updatedColis).subscribe(
          () => {
            this.snackBar.open('Colis mis à jour avec succès!', 'Fermer', {
              duration: 2000,
            });
            this.router.navigate(['/home/colislist']);
          },
          error => {
            console.error('Erreur lors de la mise à jour du colis:', error);
          }
        );
      }
    }
  }
}
