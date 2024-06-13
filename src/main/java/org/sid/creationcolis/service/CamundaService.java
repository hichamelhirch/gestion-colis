package org.sid.creationcolis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sid.creationcolis.entities.Ville;
import org.sid.creationcolis.enums.TypeLivraison;
import org.sid.creationcolis.mappers.VilleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;



@Service
public class CamundaService  {
    private static final ThreadLocal<ObjectMapper> objectMapperThreadLocal = ThreadLocal.withInitial(ObjectMapper::new);

    private final RestTemplate restTemplate;
    private final VilleMapper villeMapper;
    private final VilleService villeService;


    @Autowired
    public CamundaService(RestTemplate restTemplate, VilleMapper villeMapper, VilleService villeService) {
        this.restTemplate = restTemplate;
        this.villeMapper = villeMapper;

        this.villeService = villeService;
    }



    public Map<String, Object> prepareData(TypeLivraison typeLivraison, Long villeDepartId, Long villeDestinataireId) {
        String zoneVilleDepart = villeService.getZoneByVille(villeDepartId);
        String zoneVilleDestinataire = villeService.getZoneByVille(villeDestinataireId);
        String resultatDeuxVilles = villeService.getResultatDeuxVille(villeDepartId,villeDestinataireId);
        String natureHub = villeService.villeNaturehub(villeDepartId, villeDestinataireId);
        String villeDepartNature = villeService.getNatureVille(villeDepartId);
        String villeDestinataireNature = villeService.getNatureVille(villeDestinataireId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("TypeLivraison", Map.of("value", typeLivraison.toString(), "type", "String"));
        variables.put("RegionD", Map.of("value", zoneVilleDepart, "type", "String"));
        variables.put("RegionA", Map.of("value", zoneVilleDestinataire, "type", "String"));
        variables.put("Ville", Map.of("value", resultatDeuxVilles, "type", "String"));
        variables.put("Hub", Map.of("value", natureHub, "type", "String"));
        variables.put("VilleDepart", Map.of("value", villeDepartNature, "type", "String"));
        variables.put("VilleLiv", Map.of("value", villeDestinataireNature, "type", "String"));

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("variables", variables);

        return requestData;
    }


    public Integer getFraisFromCamunda(TypeLivraison typeLivraison, Long villeDepartId, Long villeDestinataireId) {
        try {

            Map<String, Object> requestData = prepareData(typeLivraison,villeDepartId,villeDestinataireId);


            ObjectMapper objectMapper = objectMapperThreadLocal.get();
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


}





