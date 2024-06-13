package org.sid.creationcolis.service;



import org.sid.creationcolis.dtos.HubDTO;

import org.sid.creationcolis.entities.Hub;
import org.sid.creationcolis.mappers.HubMapper;
import org.sid.creationcolis.repository.HubRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HubService {
    private HubRepository hubRepository;
    private HubMapper hubMapper;

    public HubService(HubRepository hubRepository, HubMapper hubMapper) {
        this.hubRepository = hubRepository;
        this.hubMapper = hubMapper;
    }



    public List<HubDTO> getAllHubs() {
        return hubRepository.findAll().stream()
                .map(hubMapper::toDTO)
                .collect(Collectors.toList());
    }
    public HubDTO getHubById(Long id) {
        Hub hub =hubRepository.findById(id).orElse(null);
        if (hub != null) {
            return hubMapper.toDTO(hub);
        }
        return null;
    }


    public HubDTO saveHub(HubDTO hubDTO) {
        Hub hub=hubMapper.toEntity(hubDTO);
        Hub savedHub=hubRepository.save(hub);
        return hubMapper.toDTO(savedHub);
    }


    public HubDTO updateHub(Long id,HubDTO hubDTO){
        Hub existingHub=hubRepository.findById(id).orElse(null);
        if (existingHub!=null){
            BeanUtils.copyProperties(hubDTO,existingHub,"id");
            Hub updateHub=hubRepository.save(existingHub);
            return hubMapper.toDTO(updateHub);
        }
        return null;
    }


    public void deleteHub(Long id) {
        hubRepository.deleteById(id);
    }
}
