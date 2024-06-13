package org.sid.creationcolis.web;

import org.sid.creationcolis.dtos.HubDTO;
import org.sid.creationcolis.dtos.VilleDTO;
import org.sid.creationcolis.entities.Ville;
import org.sid.creationcolis.enums.TypeLivraison;
import org.sid.creationcolis.mappers.VilleMapper;

import org.sid.creationcolis.service.CamundaService;
import org.sid.creationcolis.service.VilleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/villes")
@CrossOrigin("*")
public class VilleController {
     private final VilleService villeService;
     private final VilleMapper villeMapper;
     private final CamundaService camundaService;


    public VilleController(VilleService villeService, VilleMapper villeMapper, CamundaService camundaService) {
        this.villeService = villeService;
        this.villeMapper = villeMapper;

        this.camundaService = camundaService;
    }
    @GetMapping
    public List<VilleDTO> getAllVilles(){
        return villeService.getAllVilles();
    }
    @GetMapping("/{id}")
    public VilleDTO getVilleById(@PathVariable Long id){
        return villeService.getVilleById(id);
    }

    @GetMapping("/{id}/hub")
    public ResponseEntity<HubDTO> getHubByVilleId(@PathVariable Long id) {
        HubDTO hub = villeService.getHubByVilleId(id);
        if (hub != null) {
            return ResponseEntity.ok(hub);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/zone")
    public ResponseEntity<String> getZoneByVille(@PathVariable Long id) {
        String zone = villeService.getZoneByVille(id);
        if (!zone.equals("Ville not found")) {
            return ResponseEntity.ok(zone);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/nature")
    public ResponseEntity<String> getNatureVille(@PathVariable Long id) {
        String nature = villeService.getNatureVille(id);
        if (!nature.equals("VILLE NON TROUVÉE") && !nature.equals("AUCUN HUB ASSOCIÉ")) {
            return ResponseEntity.ok(nature);
        } else {
            return ResponseEntity.status(404).body(nature);
        }
    }

    @GetMapping("/compare")
    public ResponseEntity<String> getResultatDeuxVille(@RequestParam Long villeDepartId, @RequestParam Long villeDestinataireId) {
        String resultat = villeService.getResultatDeuxVille(villeDepartId, villeDestinataireId);
        return ResponseEntity.ok(resultat);
    }

    @GetMapping("/nature")
       public ResponseEntity<String> villeNaturehub(@RequestParam Long villeDepart,@RequestParam Long villeDestinataire){
        String result=villeService.villeNaturehub(villeDepart,villeDestinataire);
        return ResponseEntity.ok(result);
       }
  /*  @GetMapping("/search")
    public ResponseEntity<List<VilleDTO>> searchCities(@RequestParam String query) {
        List<VilleDTO> villes = villeService.searchCities(query);
        return ResponseEntity.ok(villes);
    }

   */


    @GetMapping("/getFraisFromCamunda")
    public ResponseEntity<Integer> getFraisFromCamunda(
            @RequestParam("typeLivraison") TypeLivraison typeLivraison,
            @RequestParam("villeDepartId") Long villeDepartId,
            @RequestParam("villeDestinataireId") Long villeDestinataireId) {

      //  VilleDTO villeDepartDTO = villeService.getVilleById(villeDepartId);
      //  VilleDTO villeDestinataireDTO = villeService.getVilleById(villeDestinataireId);

        Integer frais = camundaService.getFraisFromCamunda(typeLivraison, villeDepartId, villeDestinataireId);
        return ResponseEntity.ok(frais);
    }
    @GetMapping("/Allvilles")
    public List<String> searchVilleNames(String searchTerm) {
        return villeService.searchVilleNames(searchTerm);
    }
    @GetMapping("/dataFrais")
    public Map<String, Object> prepareData( @RequestParam TypeLivraison typeLivraison, @RequestParam Long villeDepartId,@RequestParam Long villeDestinataireId){
        return camundaService.prepareData(typeLivraison,villeDepartId,villeDestinataireId);
    }


    @GetMapping("/similar")
    public List<Ville> getSimilarVilles(@RequestParam String villeName) {
        System.out.println("Received request for similar villes to: " + villeName);
        List<Ville> similarVilles = villeService.findSimilarVilles(villeName);
        System.out.println("Returning similar villes: " + similarVilles);
        return similarVilles;
    }
}
