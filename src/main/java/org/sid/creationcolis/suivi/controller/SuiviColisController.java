package org.sid.creationcolis.suivi.controller;



import org.sid.creationcolis.dtos.ColisDTO;
import org.sid.creationcolis.suivi.Entity.SuiviColis;
import org.sid.creationcolis.suivi.service.SuiviColisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/suivi")
@CrossOrigin("*")
public class SuiviColisController {

    private final SuiviColisService suiviColisService;

    public SuiviColisController(SuiviColisService suiviColisService) {
        this.suiviColisService = suiviColisService;
    }

    @GetMapping("/{colisId}")
    public ResponseEntity<List<SuiviColis>> getSuiviByColisId(@PathVariable Long colisId) {
        List<SuiviColis> suiviColis = suiviColisService.getSuiviByColisId(colisId);
        return ResponseEntity.ok(suiviColis);
    }

    @PostMapping("/{colisId}/ramasser")
    public ResponseEntity<SuiviColis> ramasserColis(@PathVariable Long colisId, @RequestParam Long livreurId) {
        SuiviColis suiviColis = suiviColisService.ramasserColis(colisId, livreurId);
        return ResponseEntity.ok(suiviColis);
    }

    @PostMapping("/{colisId}/traitement")
    public ResponseEntity<SuiviColis> traiterColis(@PathVariable Long colisId, @RequestParam Long hubId) {
        SuiviColis suiviColis = suiviColisService.traiterColis(colisId, hubId);
        return ResponseEntity.ok(suiviColis);
    }
    @PostMapping("/{colisId}/enCoursDeLivraison")
    public ResponseEntity<SuiviColis>  mettreEnCoursDeLivraison(@PathVariable Long colisId, @RequestParam Long livreurId) {
        SuiviColis suiviColis = suiviColisService.mettreEnCoursDeLivraison(colisId, livreurId);
        return ResponseEntity.ok(suiviColis);
    }

   /*@PostMapping("/{colisId}/livrer")
    public ResponseEntity<SuiviColis> livrerColis(@PathVariable Long colisId, @RequestParam Long livreurId) {
        SuiviColis suiviColis = suiviColisService.livrerColis(colisId, livreurId);
        return ResponseEntity.ok(suiviColis);
    }

    */
   @PostMapping("/{colisId}/livrer")
   public ResponseEntity<SuiviColis> livrerColis(@PathVariable Long colisId, @RequestParam Long livreurId) throws IOException {
       SuiviColis suiviColis = suiviColisService.livrerColis(colisId, livreurId);
       return ResponseEntity.ok(suiviColis);
   }
    @PutMapping("/{colisId}/probleme")
    public ResponseEntity<SuiviColis> updateDescriptionProbleme(@PathVariable Long colisId,
                                                                @RequestParam String description,
                                                                @RequestParam String type,
                                                                @RequestParam Long id) {
        SuiviColis suiviColis = suiviColisService.updateDescriptionProbleme(colisId, description, type, id);
        return ResponseEntity.ok(suiviColis);
    }



  /*  @PostMapping("/{colisId}/livrerWhatssap")
    public ResponseEntity<SuiviColis> livrerColiswht(@PathVariable Long colisId, @RequestParam Long livreurId) throws IOException {
        SuiviColis suiviColis = suiviColisService.livrerColisWhatSap(colisId, livreurId);
        return ResponseEntity.ok(suiviColis);
    }

   */


    @GetMapping("/operations/{barcode}")
    public ResponseEntity<List<String>> getOperationsByBarcode(@PathVariable String barcode) {
        List<String> operations = suiviColisService.getOperationsByBarcode(barcode);
        return ResponseEntity.ok(operations);
    }
    @GetMapping("/colis-livres")
    public List<ColisDTO> getAllColisLivres() {
        return suiviColisService.getAllColisLivres();
    }
}
