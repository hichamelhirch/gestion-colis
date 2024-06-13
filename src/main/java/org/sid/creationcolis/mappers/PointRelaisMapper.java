package org.sid.creationcolis.mappers;

import org.sid.creationcolis.dtos.PointRelaisDTO;
import org.sid.creationcolis.entities.PointRelais;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PointRelaisMapper {

    private final VilleMapper villeMapper;


    public PointRelaisMapper(VilleMapper villeMapper) {
        this.villeMapper = villeMapper;

    }

    public PointRelais toEntity(PointRelaisDTO dto) {
        PointRelais pointRelais = new PointRelais();
        pointRelais.setVille(villeMapper.toEntity(dto.getVille()));
        BeanUtils.copyProperties(dto, pointRelais);

        return pointRelais;
    }

    public PointRelaisDTO toDTO(PointRelais pointRelais) {
        PointRelaisDTO dto = new PointRelaisDTO();
       dto.setVille(villeMapper.toDTO(pointRelais.getVille()));
        BeanUtils.copyProperties(pointRelais, dto);

        return dto;
    }
}
