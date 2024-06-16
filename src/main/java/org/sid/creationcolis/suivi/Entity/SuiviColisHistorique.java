package org.sid.creationcolis.suivi.Entity;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.sid.creationcolis.entities.Colis;
import org.sid.creationcolis.suivi.enums.StatutSuiviColis;

import java.time.LocalDateTime;

@Entity
@Getter @Setter

public class SuiviColisHistorique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Colis colis;

    @Enumerated(EnumType.STRING)
     private StatutSuiviColis statut;
    private LocalDateTime dateSuivi;
    private String description;
}
