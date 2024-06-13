package org.sid.creationcolis.mappers;

import org.sid.creationcolis.dtos.HubDTO;
import org.sid.creationcolis.dtos.ServiceALivraisonDTO;
import org.sid.creationcolis.entities.Hub;
import org.sid.creationcolis.entities.ServiceALivraison;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ServiceALivraisonMapper {


    public ServiceALivraison toEntity(ServiceALivraisonDTO dto){
        ServiceALivraison serviceALivraison = new ServiceALivraison();
        BeanUtils.copyProperties(dto, serviceALivraison);
        return serviceALivraison;
    }
    public ServiceALivraisonDTO toDTO(ServiceALivraison serviceALivraison){
        ServiceALivraisonDTO dto = new ServiceALivraisonDTO();
        BeanUtils.copyProperties(serviceALivraison, dto);
        return dto;
    }

}
