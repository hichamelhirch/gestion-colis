package org.sid.creationcolis.entities;

import jakarta.persistence.*;
import lombok.*;
import org.sid.creationcolis.enums.TypeChargeur;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointRelais  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adresse;
    private String nomPointRelai;
    private String numeroTel;

    @ManyToOne
    private Ville ville;

}
