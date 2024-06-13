package org.sid.creationcolis.mappers;

import org.sid.creationcolis.dtos.RegionDTO;
import org.sid.creationcolis.dtos.VilleDTO;
import org.sid.creationcolis.entities.Region;
import org.sid.creationcolis.entities.Ville;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class VilleMapper {

    public Ville toEntity(VilleDTO villeDTO) {
        Ville ville = new Ville();
        BeanUtils.copyProperties(villeDTO, ville);

        return ville;
    }

    public VilleDTO toDTO(Ville ville) {
        VilleDTO dto = new VilleDTO();
        BeanUtils.copyProperties(ville, dto);

        return dto;
    }
}
