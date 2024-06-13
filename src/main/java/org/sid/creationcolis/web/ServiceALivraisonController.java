package org.sid.creationcolis.web;

import org.sid.creationcolis.dtos.ServiceALivraisonDTO;
import org.sid.creationcolis.entities.ServiceALivraison;
import org.sid.creationcolis.service.ServiceALivraisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin("*")
public class ServiceALivraisonController {
    @Autowired
    private ServiceALivraisonService serviceALivraisonService;

    @PostMapping
    public ServiceALivraisonDTO createService(@RequestBody ServiceALivraisonDTO service) {
        return serviceALivraisonService.saveServiceLivraions(service);
    }

    @GetMapping
    public List<ServiceALivraisonDTO> getAllServices() {
        return serviceALivraisonService.getAllServices();
    }
    @GetMapping("/{id}")
    public ServiceALivraisonDTO getServiceById(@PathVariable  Long id){
        return serviceALivraisonService.getServiceById(id);
    }
}
