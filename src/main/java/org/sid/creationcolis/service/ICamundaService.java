package org.sid.creationcolis.service;

import org.sid.creationcolis.dtos.VilleDTO;
import org.sid.creationcolis.enums.TypeLivraison;

public interface ICamundaService {
    Integer getFraisFromCamunda(TypeLivraison typeLivraison, VilleDTO villeDepartDTO, VilleDTO villeDestinataireDTO);
}
