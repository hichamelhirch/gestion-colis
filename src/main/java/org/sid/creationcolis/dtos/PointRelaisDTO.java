package org.sid.creationcolis.dtos;

import lombok.Getter;
import lombok.Setter;
import org.sid.creationcolis.enums.TypeChargeur;

@Getter
@Setter
public class PointRelaisDTO {
    private Long id;
    private String adresse;
    private String nomPointRelai;
    private String numeroTel;
    private VilleDTO ville;
    private TypeChargeur typeChargeur;

    //  private UserDTO user;
}
