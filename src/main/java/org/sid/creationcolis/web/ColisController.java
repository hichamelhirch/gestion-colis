package org.sid.creationcolis.web;

import org.sid.creationcolis.dtos.ColisDTO;
import org.sid.creationcolis.entities.Client;
import org.sid.creationcolis.entities.Colis;
import org.sid.creationcolis.entities.Ville;
import org.sid.creationcolis.enums.StatutColis;
import org.sid.creationcolis.service.ColisService;
import org.sid.creationcolis.service.LabelService;
import org.sid.creationcolis.service.VilleService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/colis")
@CrossOrigin("*")
public class ColisController {

    private final ColisService colisService;
    private final VilleService villeService;
    private final LabelService labelService;

    public ColisController(ColisService colisService, VilleService villeService, LabelService labelService) {
        this.colisService = colisService;
        this.villeService = villeService;
        this.labelService = labelService;
    }

    @PostMapping
    public ResponseEntity<ColisDTO> createColis(@RequestBody ColisDTO colisDTO) {
        ColisDTO savedColis = colisService.saveColis(colisDTO);
        return ResponseEntity.ok(savedColis);
    }

    @PostMapping("/import")
    public ResponseEntity<List<ColisDTO>> importColis(@RequestBody List<ColisDTO> colisDTOList) {
        try {
            List<ColisDTO> savedColisDTOList = colisService.saveColisDTOList(colisDTOList);
            return ResponseEntity.ok(savedColisDTOList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/stk")
    public ColisDTO creatTeste(@RequestBody ColisDTO colisDTO) {
        return colisService.saveColisTest(colisDTO);
    }

    @GetMapping
    public ResponseEntity<List<ColisDTO>> getAllColis() {
        List<ColisDTO> colis = colisService.getAllColis();
        return ResponseEntity.ok(colis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColisDTO> getColisById(@PathVariable Long id) {
        ColisDTO colis = colisService.getColisById(id);
        if (colis != null) {
            return ResponseEntity.ok(colis);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColisDTO> updateColis(@PathVariable Long id, @RequestBody ColisDTO colisDTO) {
        ColisDTO updatedColis = colisService.updateColis(id, colisDTO);
        if (updatedColis != null) {
            return ResponseEntity.ok(updatedColis);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColis(@PathVariable Long id) {
        colisService.deleteColis(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Colis> createColis(@RequestBody Client client, @RequestParam String nomDestinataire,
                                             @RequestParam String prenomDestinataire, @RequestParam String telDestinataire,
                                             @RequestParam String adresseDestinataire, @RequestParam Ville villeDestinataire) {
        Colis colis = colisService.createColisFromClient(client, nomDestinataire, prenomDestinataire, telDestinataire, adresseDestinataire, villeDestinataire);
        return ResponseEntity.ok(colis);
    }

    @GetMapping("/getData/{colisId}")
    public Map<String, Object> prepareDataForCamunda(@PathVariable Long colisId) {
        return colisService.prepareDataForCamunda(colisId);
    }

    @GetMapping("/getFrais/{colisId}")
    public Integer getFraisFromCamunda(@PathVariable Long colisId) {
        return colisService.getFraisFromCamunda(colisId);
    }

    @GetMapping("/{id}/barcode")
    public ResponseEntity<String> getBarcode(@PathVariable Long id) {
        ColisDTO colis = colisService.getColisById(id);
        String barcode=colis.getCodeBarre();
        return ResponseEntity.ok(barcode);
    }

    @GetMapping("/search")
    public ResponseEntity<ColisDTO> getColisByNumeroColis(@RequestParam String numeroColis) {
        ColisDTO colisDTO = colisService.getColisByNumeroColis(numeroColis);
        return ResponseEntity.ok(colisDTO);
    }

  /*  @PostMapping("/importCSV")
    public ResponseEntity<Map<String, Object>> importCSV(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> response = colisService.createColisEnMasse(file);
            response.put("success", true);
            response.put("message", "Import successful.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

   */

    @PostMapping("/importCSV")
    public ResponseEntity<Map<String, Object>> importCSV(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> response = colisService.createColisEnMasse(file);
            response.put("success", true);
            response.put("message", "Import successful.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }



    @GetMapping("/generate-label/{id}")
    public ResponseEntity<ColisDTO> generateLabel(@PathVariable Long id) {
        try {
            ColisDTO colisDTO = colisService.generateLabel(id);
            return ResponseEntity.ok(colisDTO);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/download-label/{id}")
    public ResponseEntity<Resource> downloadLabel(@PathVariable Long id) {
        try {
            return colisService.downloadLabel(id);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/searchByCodeBarre")
    public ResponseEntity<ColisDTO> getColisByCodeBarre(@RequestParam String codeBarre) {
        ColisDTO colisDTO = colisService.getColisByCodeBarre(codeBarre);
        if (colisDTO != null) {
            return ResponseEntity.ok(colisDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

  @PostMapping("/updateStatus/{id}")
  public void updateColisStatusToConfirmer(@PathVariable Long id){
      colisService.updateColisStatusToConfirmed(id);
  }

    @PostMapping("/{id}/addService")
    public ResponseEntity<ColisDTO> addServiceToColis(@PathVariable Long id, @RequestParam Long serviceId) {
        ColisDTO updatedColis = colisService.addServiceToColis(id, serviceId);
        return ResponseEntity.ok(updatedColis);
    }

    @PostMapping("/{colisId}/removeServices")
    public ResponseEntity<Void> removeServices(@PathVariable Long colisId, @RequestParam Long serviceId) {
        try {
            colisService.removeServices(colisId, serviceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/updateStatusToCancel/{id}")
    public void updateColisStatusToCancel(@PathVariable Long id){
        colisService.updateColisStatusToCancel(id);
    }

    @GetMapping("/download-multiple-labels")
    public ResponseEntity<ByteArrayResource> downloadMultipleLabels(@RequestParam List<Long> colisIds) throws IOException {
        ByteArrayOutputStream out = colisService.generateMultipleLabelsPDF(colisIds);
        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etiquettes-combinees.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(resource.contentLength())
                .body(resource);
    }
    @GetMapping("/filter")
    public List<ColisDTO> filterColis(@RequestParam List<StatutColis> status) {
        return colisService.filterColis(status);
    }
}
