package org.sid.creationcolis.entities;

import jakarta.persistence.*;
import lombok.*;
import org.sid.creationcolis.enums.TypeChargeur;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adresseClient;
    private String nomClient;
    private String tel;
    @Enumerated(EnumType.STRING)
    private TypeChargeur typeChargeur;

    @ManyToOne
    @JoinColumn(name = "ville_client_id")
    private Ville villeClient;



}
