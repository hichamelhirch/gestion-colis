package org.sid.creationcolis.mappers;

import org.sid.creationcolis.dtos.RegionDTO;
import org.sid.creationcolis.entities.Region;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class RegionMapper {

    public Region toEntity(RegionDTO regionDTO) {
        Region region = new Region();
        BeanUtils.copyProperties(regionDTO, region);
        return region;
    }

    public RegionDTO toDTO(Region region) {
        RegionDTO dto = new RegionDTO();
        BeanUtils.copyProperties(region, dto);
        return dto;
    }
}
