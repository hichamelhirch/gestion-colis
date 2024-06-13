package org.sid.creationcolis.entities;

import jakarta.persistence.*;
import lombok.*;
import org.sid.creationcolis.enums.Zone;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String region;
    @Enumerated(EnumType.STRING)
    private Zone zone;

}
