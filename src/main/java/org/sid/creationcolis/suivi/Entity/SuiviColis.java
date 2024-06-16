package org.sid.creationcolis.suivi.Entity;



import jakarta.persistence.*;
import lombok.*;
import org.sid.creationcolis.entities.Colis;
import org.sid.creationcolis.entities.Hub;
import org.sid.creationcolis.suivi.enums.StatutSuiviColis;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SuiviColis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;

    @Enumerated(EnumType.STRING)
    private StatutSuiviColis statut;

    private LocalDateTime dateSuivi;

    private String description;

    @ManyToOne
    @JoinColumn(name = "hub_id", nullable = true)
    private Hub hub;

    @ManyToOne
    @JoinColumn(name = "livreur_id", nullable = true)
    private Livreur livreur;
    // ce champ est a remplir dans le cas d'un prblm
    private String descriptionProbleme;
}
