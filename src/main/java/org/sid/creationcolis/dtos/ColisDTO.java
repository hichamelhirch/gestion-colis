package org.sid.creationcolis.dtos;


import lombok.Getter;
import lombok.Setter;

import org.sid.creationcolis.enums.StatutColis;

import org.sid.creationcolis.enums.TypeLivraison;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ColisDTO {
    private Long id;
    private String codeBarre;
    private StatutColis statusColis;
    private TypeLivraison typeLivraison;
    private double cashDelivery;
    private String numeroColis;
    private double poids;
    private double longueur;
    private String description;

    private double valeurColis;
    private double largeur;
    private double hauteur;
    private double frais;
    private LocalDateTime creationDate;
   // private TypeChargeur typeChargeur;
    private ClientDTO client;
   // private PointRelaisDTO pointRelais;
   // Adresse de Livraison

   private VilleDTO villeDestinataire;
    private String nomDestinataire;
    private String telDestinataire;
    private String adresseDestinataire;
    private String adresse2Destinataire;

    // Adresse d'enlevement'

    private VilleDTO villeDepart;
    private String numTelChargeur;
    private String premiereAdresseChargeur;
    private String deuxiemeAdresseChargeur;
    private List<ServiceALivraisonDTO> services=new ArrayList<>();



   private String paymentToken;
    private boolean estPaye;
   // private LocalDateTime tokenExpirationDate;


}
