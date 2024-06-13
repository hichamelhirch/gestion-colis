package org.sid.creationcolis.service;

import org.sid.creationcolis.dtos.VilleDTO;
import org.sid.creationcolis.entities.Ville;

public interface IVilleService {
    VilleDTO getVilleById(Long id);
    String getZoneByVille(Long villeId);
    String getNatureVille(Long villeId);
    String getResultatDeuxVille(Long villeDepartId, Long villeDestinataireId);
    String villeNaturehub(Long villeDepartId, Long villeDestinataireId);
}
