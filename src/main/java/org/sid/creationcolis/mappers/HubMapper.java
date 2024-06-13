package org.sid.creationcolis.mappers;

import org.sid.creationcolis.dtos.HubDTO;
import org.sid.creationcolis.entities.Hub;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class HubMapper {
    public Hub toEntity(HubDTO dto){
        Hub hub=new Hub();
        BeanUtils.copyProperties(dto,hub);
        return hub;
    }
    public HubDTO toDTO(Hub hub){
        HubDTO hubDTO=new HubDTO();
        BeanUtils.copyProperties(hub,hubDTO);
        hubDTO.setAdresse(hub.getAdresse());
        return hubDTO;
    }
}
