package org.sid.creationcolis.web;



import org.sid.creationcolis.dtos.HubDTO;
import org.sid.creationcolis.dtos.PointRelaisDTO;
import org.sid.creationcolis.service.HubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hubs")
@CrossOrigin("*")
public class HubController {

    private HubService hubService;

    public HubController(HubService hubService) {
        this.hubService = hubService;
    }


    @PostMapping
    public HubDTO createHub(@RequestBody HubDTO hubDTO) {
       HubDTO savedHub=hubService.saveHub(hubDTO);
        return savedHub;
    }
    @GetMapping
    public List<HubDTO> getAllHubs(){
        List<HubDTO> hubDTOList=hubService.getAllHubs();
        return hubDTOList;
    }
    @GetMapping("/{id}")
    public HubDTO getHubById(@PathVariable  Long id){
        HubDTO hub=hubService.getHubById(id);
        if (hub!=null){
            return hub;
        } return null;
    }
}
