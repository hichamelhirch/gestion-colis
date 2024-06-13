package org.sid.creationcolis.service;

import org.sid.creationcolis.dtos.HubDTO;
import org.sid.creationcolis.dtos.ServiceALivraisonDTO;
import org.sid.creationcolis.entities.Hub;
import org.sid.creationcolis.entities.ServiceALivraison;
import org.sid.creationcolis.mappers.ServiceALivraisonMapper;
import org.sid.creationcolis.repository.ServiceALivraisonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceALivraisonService {

    private final ServiceALivraisonRepository serviceALivraisonRepository;
    private final ServiceALivraisonMapper serviceALivraisonMapper;

    public ServiceALivraisonService(ServiceALivraisonRepository serviceALivraisonRepository, ServiceALivraisonMapper serviceALivraisonMapper) {
        this.serviceALivraisonRepository = serviceALivraisonRepository;

        this.serviceALivraisonMapper = serviceALivraisonMapper;
    }

    public ServiceALivraison saveService(ServiceALivraison service) {
        return serviceALivraisonRepository.save(service);
    }


    public List<ServiceALivraisonDTO> getAllServices() {

        return serviceALivraisonRepository.findAll()
                .stream().map(serviceALivraisonMapper::toDTO).collect(Collectors.toList());
    }
    public ServiceALivraisonDTO saveServiceLivraions(ServiceALivraisonDTO serviceALivraisonDTO) {
        ServiceALivraison serviceALivraison=serviceALivraisonMapper.toEntity(serviceALivraisonDTO);
        ServiceALivraison serviceALivraison1=serviceALivraisonRepository.save(serviceALivraison);
        return serviceALivraisonMapper.toDTO(serviceALivraison1);
    }
    public ServiceALivraisonDTO getServiceById(Long id){
        ServiceALivraison serviceALivraison=serviceALivraisonRepository.findById(id).orElse(null);

        return  serviceALivraisonMapper.toDTO(serviceALivraison);
    }

}
