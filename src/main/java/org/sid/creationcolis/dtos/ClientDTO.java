package org.sid.creationcolis.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.sid.creationcolis.enums.TypeChargeur;

@Getter
@Setter
public class ClientDTO {
    private Long id;
    private String adresseClient;
   // private String dateNaissance;
    private String password;

    private String email;
    private String nomClient;
  //  private String prenomClient;
    private String tel;
   private TypeChargeur typeChargeur;
    private VilleDTO villeClient;

}
