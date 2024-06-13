package org.sid.creationcolis.service;

import org.sid.creationcolis.dtos.ClientDTO;
import org.sid.creationcolis.entities.Client;
import org.sid.creationcolis.entities.ServiceALivraison;
import org.sid.creationcolis.mappers.ClientMapper;
import org.sid.creationcolis.repository.ClientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }

  /*  public Client findOrCreateClient(ClientDTO clientDTO) {
        Optional<Client> existingClient = clientRepository.findByEmail(clientDTO.getEmail());
        return existingClient.orElseGet(() -> clientRepository.save(clientMapper.toEntity(clientDTO)));
    }

   */

    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client != null) {
            return clientMapper.toDTO(client);
        }
        return null;
    }

    public ClientDTO saveClient(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toDTO(savedClient);
    }

    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(id).orElse(null);
        if (existingClient != null) {
            BeanUtils.copyProperties(clientDTO, existingClient, "id");
            Client updatedClient = clientRepository.save(existingClient);
            return clientMapper.toDTO(updatedClient);
        }
        return null;
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
    public ClientDTO getClientByUserId(Long userId) {
        Optional<Client> client = clientRepository.findByUserId(userId);
        return client.map(clientMapper::toDTO).orElse(null);
    }

}
