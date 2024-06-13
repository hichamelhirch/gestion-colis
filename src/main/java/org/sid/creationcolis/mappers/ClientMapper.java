package org.sid.creationcolis.mappers;

import org.sid.creationcolis.dtos.ClientDTO;
import org.sid.creationcolis.entities.Client;
import org.sid.creationcolis.entities.Ville;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
@Component
public class ClientMapper {

    private final VilleMapper villeMapper;

    public ClientMapper(VilleMapper villeMapper) {
        this.villeMapper = villeMapper;
    }

    public Client toEntity(ClientDTO dto) {
        Client client = new Client();
        BeanUtils.copyProperties(dto, client);

        // Conversion de VilleDTO en Ville si dto.getVilleClient() est non null
        if (dto.getVilleClient() != null) {
            client.setVilleClient(villeMapper.toEntity(dto.getVilleClient()));
        }

        return client;
    }

    public ClientDTO toDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        BeanUtils.copyProperties(client, dto);

        // Conversion de Ville en VilleDTO si client.getVilleClient() est non null
       if (client.getVilleClient() != null) {
            dto.setVilleClient(villeMapper.toDTO(client.getVilleClient()));
        }



        return dto;
    }
}
