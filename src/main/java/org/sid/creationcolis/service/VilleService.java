package org.sid.creationcolis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sid.creationcolis.dtos.HubDTO;
import org.sid.creationcolis.dtos.VilleDTO;
import org.sid.creationcolis.entities.Hub;
import org.sid.creationcolis.entities.Ville;
import org.sid.creationcolis.enums.TypeLivraison;
import org.sid.creationcolis.mappers.HubMapper;
import org.sid.creationcolis.mappers.VilleMapper;
import org.sid.creationcolis.repository.VilleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VilleService {


    private final VilleRepository villeRepository;
    private final VilleMapper villeMapper;
    private final HubMapper hubMapper;
    private final RestTemplate restTemplate;

    @Autowired
    public VilleService(VilleRepository villeRepository, VilleMapper villeMapper, HubMapper hubMapper, RestTemplate restTemplate) {
        this.villeRepository = villeRepository;

        this.villeMapper = villeMapper;
        this.hubMapper = hubMapper;

        this.restTemplate = restTemplate;
    }


    public List<VilleDTO> getAllVilles() {
        return villeRepository.findAll().stream()
                .map(villeMapper::toDTO)
                .collect(Collectors.toList());
    }


    public VilleDTO getVilleById(Long id) {
        Ville ville= villeRepository.findById(id).orElse(null);
        if (ville != null) {
            return villeMapper.toDTO(ville
            );
        }
        return null;
    }


    public HubDTO getHubByVilleId(Long villeId) {
        Hub hub = villeRepository.findHubByVilleId(villeId);
        if (hub != null) {
            return hubMapper.toDTO(hub);
        }
        return null;
    }

    public String getZoneByVille(Long villeId) {
        Optional<Ville> villeOpt = villeRepository.findById(villeId);
        if (villeOpt.isPresent()) {
            Ville ville = villeOpt.get();
            return ville.getRegion().getZone().name();
        } else {
            return "Ville not found";
        }
    }

    public String getNatureVille(Long villeId) {
        Ville ville = villeRepository.findById(villeId).orElse(null);
        if (ville != null) {
            Hub hub = villeRepository.findHubByVilleId(villeId);
            if (hub != null) {
                String villeName = ville.getVille().toLowerCase();
                String hubName = hub.getNameHub().toLowerCase();
                if (hubName.contains(villeName)) {
                    return "Centrale";
                } else {
                    return "PERIPHERIQUE";
                }
            } else {
                return "AUCUN HUB ASSOCIÉ";
            }
        } else {
            return "VILLE NON TROUVÉE";
        }
    }

    public String getResultatDeuxVille(Long villeDepartId, Long villeDestinataireId) {
        Optional<Ville> villeDepartOpt = villeRepository.findById(villeDepartId);
        Optional<Ville> villeDestinataireOpt = villeRepository.findById(villeDestinataireId);

        if (villeDepartOpt.isPresent() && villeDestinataireOpt.isPresent()) {
            Ville villeDepart = villeDepartOpt.get();
            Ville villeDestinataire = villeDestinataireOpt.get();

            if (villeDepart.getVille().equalsIgnoreCase(villeDestinataire.getVille())) {
                return "Meme Villes";
            } else {
                return "Villes differentes";
            }
        } else {
            if (!villeDepartOpt.isPresent() && !villeDestinataireOpt.isPresent()) {
                return "Les deux villes n'ont pas été trouvées";
            } else if (!villeDepartOpt.isPresent()) {
                return "Ville de départ non trouvée";
            } else {
                return "Ville de destination non trouvée";
            }
        }
    }




  /*  public Integer getFraisFromCamunda(TypeLivraison typeLivraison, VilleDTO villeDepartDTO, VilleDTO villeDestinataireDTO) {
        try {
            Ville villeDepart = villeMapper.toEntity(villeDepartDTO);
            Ville villeDestinataire = villeMapper.toEntity(villeDestinataireDTO);
            Map<String, Object> requestData = prepareData(typeLivraison, villeDepart, villeDestinataire);
            ObjectMapper objectMapper = new ObjectMapper();
            String requestDataJson = objectMapper.writeValueAsString(requestData);
            String camundaDecisionUrl = "http://localhost:8080/engine-rest/decision-definition/key/Decision_1gnjh82/evaluate";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestDataJson, headers);
            ResponseEntity<Map[]> responseEntity = restTemplate.exchange(
                    camundaDecisionUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map[].class
            );
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> response = responseEntity.getBody()[0];
                if (response != null && response.containsKey("Frais")) {
                    Map<String, Object> fraisMap = (Map<String, Object>) response.get("Frais");
                    if (fraisMap != null && fraisMap.containsKey("value")) {
                        return (Integer) fraisMap.get("value");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


   */





    public String villeNaturehub(Long villeDepartId, Long villeDestinataireId) {

        Ville villeDepart = villeRepository.findById(villeDepartId).orElse(null);

        Ville villeDestinataire = villeRepository.findById(villeDestinataireId).orElse(null);

        if (villeDepart != null && villeDestinataire != null) {

            Hub hubVilleDepart = villeDepart.getHubVille();

            Hub hubVilleDestinataire = villeDestinataire.getHubVille();

            if (hubVilleDepart != null && hubVilleDestinataire != null) {
                String nomHubVilleDepart = hubVilleDepart.getNameHub();
                String nomHubVilleDestinataire = hubVilleDestinataire.getNameHub();

                if (nomHubVilleDepart.equals(nomHubVilleDestinataire)) {
                    return "Meme hubs";
                } else {
                    return "Different hubs";
                }
            } else {
                return "Hub non trouvé pour au moins l'une des villes";
            }
        } else {
            return "Ville non trouvée pour au moins l'une des villes";
        }
    }
    public List<String> searchVilleNames(String searchTerm) {
        return villeRepository.searchVilleNamesIgnoreCase(searchTerm);
    }



    public Ville getOrCreateVille(String nom) {
        Ville ville = villeRepository.findByVille(nom);
        if (ville == null) {
            ville = new Ville();
            ville.setVille(nom);
            ville = villeRepository.save(ville);
        }
        return ville;
    }



    public List<Ville> findSimilarVilles(String villeName) {
        List<Ville> allVilles = villeRepository.findAll();
        System.out.println("All Villes: " + allVilles);

        List<Ville> similarVilles = allVilles.stream()
                .filter(ville -> {
                    int distance = calculateLevenshteinDistance(ville.getVille().toLowerCase(), villeName.toLowerCase());
                    System.out.println("Comparing " + ville.getVille() + " with " + villeName + " => Distance: " + distance);
                    return distance <= 5;
                })
                .collect(Collectors.toList());

        System.out.println("Similar Villes: " + similarVilles);
        return similarVilles;
    }
    // pour les villes similaires
    private int calculateLevenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            for (int j = 0; j <= str2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }

        return dp[str1.length()][str2.length()];
    }
}
