package org.sid.creationcolis.entities;

import jakarta.persistence.*;

import lombok.*;
import org.sid.creationcolis.enums.StatutColis;

import org.sid.creationcolis.enums.TypeLivraison;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Colis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 /*

 @Column(unique = true, nullable = false)
    private String codeBarre;
  */

 /*  @Lob
   @Column(columnDefinition = "LONGBLOB")
   private byte[] codeBarre;

  */

    @Column(unique = true, nullable = true, length = 12)
    private String codeBarre;

    @Column(unique = true, nullable = false)
    private String numeroColis;
    @Enumerated(EnumType.STRING)
    private StatutColis statusColis;
    @Enumerated(EnumType.STRING)
    private TypeLivraison typeLivraison;
    private double cashDelivery;
    private double poids;
    private double longueur;
    private double largeur;
    private double hauteur;
    private double frais;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;
    private LocalDateTime creationDate;



    // Adresse de Livraison
    @ManyToOne
    private Ville villeDestinataire;
    private String nomDestinataire;
    private String telDestinataire;
    private String adresseDestinataire;
    private String adresse2Destinataire;
    private String description;
    private double valeurColis;


 // Adresse d'enlevement'
    @ManyToOne
    private Ville villeDepart;
    private String numTelChargeur;
    private String premiereAdresseChargeur;
    private String deuxiemeAdresseChargeur;

  // paiement
  @Column(unique = true, nullable = false)
  private String paymentToken;
    @Column(nullable = true)
    private String groupPaymentToken;

    @Column(nullable = false)
    private boolean estPaye;

    //private LocalDateTime tokenExpirationDate;


    @ManyToMany
    @JoinTable(
            name = "colis_servicealivraison",
            joinColumns = @JoinColumn(name = "colis_id"),
            inverseJoinColumns = @JoinColumn(name = "servicealivraison_id"))
    private List<ServiceALivraison> services;





    @PrePersist
    public void generateNumeroColis() {
        if (this.numeroColis == null) {
            this.numeroColis = String.format("Colis/%06d", System.currentTimeMillis() % 1000000);
        }
    }


}
