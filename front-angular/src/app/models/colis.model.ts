// colis.model.ts
export interface Colis {
  id?: number;
  codeBarre: string;
  statusColis: StatutColis;
  typeLivraison: TypeLivraison;
  cashDelivery: number;
  poids: number;
  longueur: number;
  largeur: number;
  hauteur: number;
  frais: number;
  client: Client;
  nomDestinataire: string;
  telDestinataire: string;
  adresseDestinataire: string;
  adresse2Destinataire: string;
  description: string;
  valeurColis: number;
  villeDestinataire: Ville;
  creationDate: Date;

  // Attributs d'adresse d'enlèvement
  villeDepart: Ville;
  numTelChargeur: string;
  premiereAdresseChargeur: string;
  deuxiemeAdresseChargeur: string;
  numeroColis:string;
  services: ServiceALivraison[];
}

// Additional related models
export interface Client {
  id?: number;
  nomClient: string;
  //prenomClient: string;
  adresseClient: string;
  // dateNaissance: string;
  email: string;
  tel: string;
  password: string;
  typeChargeur: TypeChargeur;
  villeClient: Ville;

}
export interface ServiceALivraison{
  id:number;
  serviceName:string;
  description: string;
  price:number;
  status:string;

}
export interface PointRelaisDTO {
  id: number;
  adresse: string;
  nomPointRelai: string;
  numeroTel: string;
  ville: Ville;
}

export interface Ville {
  id: number;
  ville: string;
}

export interface RegionDTO {
  id: number;
  region: string;
}

// Enums
export enum StatutColis {
  BROUILLON,
  CONFIRMER,
  ANNULER
}
// colis.model.ts

export enum TypeChargeur {
  PR,
  CS
}

export enum TypeLivraison {
  LPR = 'LPR',
  LAD = 'LAD'
}

export enum Zone {
  NORD,
  SUD
}
