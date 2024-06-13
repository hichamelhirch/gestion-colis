package org.sid.creationcolis.mappers;

import org.sid.creationcolis.dtos.ColisDTO;
import org.sid.creationcolis.dtos.ServiceALivraisonDTO;
import org.sid.creationcolis.entities.Colis;
import org.sid.creationcolis.entities.ServiceALivraison;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ColisMapper {

    private final VilleMapper villeMapper;
    private final ServiceALivraisonMapper serviceALivraisonMapper;
    private final ClientMapper clientMapper;

    public ColisMapper(VilleMapper villeMapper, ServiceALivraisonMapper serviceALivraisonMapper, ClientMapper clientMapper) {
        this.villeMapper = villeMapper;
        this.serviceALivraisonMapper = serviceALivraisonMapper;
        this.clientMapper = clientMapper;
    }

    public Colis toEntity(ColisDTO dto) {
        Colis colis = new Colis();
        BeanUtils.copyProperties(dto, colis);

        if (dto.getVilleDestinataire() != null && dto.getVilleDepart()!=null) {
            colis.setVilleDestinataire(villeMapper.toEntity(dto.getVilleDestinataire()));
            colis.setVilleDepart(villeMapper.toEntity(dto.getVilleDepart()));
        }

        if (dto.getClient() != null) {
            colis.setClient(clientMapper.toEntity(dto.getClient()));
        }
        if (dto.getServices() != null) {
            colis.setServices(dto.getServices().stream()
                    .map(serviceALivraisonMapper::toEntity)
                    .collect(Collectors.toList()));
        }


        return colis;
    }

    public ColisDTO toDTO(Colis colis) {
        ColisDTO dto = new ColisDTO();
        BeanUtils.copyProperties(colis, dto);

        if (colis.getVilleDestinataire() != null && colis.getVilleDepart()!=null) {
            dto.setVilleDestinataire(villeMapper.toDTO(colis.getVilleDestinataire()));
            dto.setVilleDepart(villeMapper.toDTO(colis.getVilleDepart()));
        }
        if (colis.getClient() != null) {
            dto.setClient(clientMapper.toDTO(colis.getClient()));
        }
        if (colis.getServices() != null) {
            dto.setServices(colis.getServices().stream()
                    .map(serviceALivraisonMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
