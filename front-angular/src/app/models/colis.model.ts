
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

export enum StatutColis {
  CONFIRMER = 'CONFIRMER',
  BROUILLON = 'BROUILLON',
  ANNULER = 'ANNULER'
}


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

export enum StatutSuiviColis {
  A_RAMASSER = 'A_RAMASSER',
  EN_COURS_DE_TRAITEMENT = 'EN_COURS_DE_TRAITEMENT',
  LIVRE = 'LIVRE'
}




export interface SuiviColis {
 id:number;
  colis:Colis;
   statut:StatutSuiviColis;
  dateSuivi:Date;
  description:string;
    hub:Hub;
    livreur:Livreur
   descriptionProbleme:string;
}
export interface Hub{

  id:number;
  nameHub:string;
  numeroTelHub:string;
  adresse:string;
}
export interface Livreur{
  id:number;

  nomComplet:string;

  tel:string;
}
