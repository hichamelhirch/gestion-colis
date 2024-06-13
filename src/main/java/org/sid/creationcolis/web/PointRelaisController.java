package org.sid.creationcolis.web;

import org.sid.creationcolis.dtos.PointRelaisDTO;
import org.sid.creationcolis.service.PointRelaisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pointrelais")
@CrossOrigin("*")
public class PointRelaisController {

    private final PointRelaisService pointRelaisService;

    public PointRelaisController(PointRelaisService pointRelaisService) {
        this.pointRelaisService = pointRelaisService;
    }

    @PostMapping
    public ResponseEntity<PointRelaisDTO> createPointRelais(@RequestBody PointRelaisDTO pointRelaisDTO) {
        PointRelaisDTO savedPointRelais = pointRelaisService.savePointRelais(pointRelaisDTO);
        return ResponseEntity.ok(savedPointRelais);
    }

    @GetMapping
    public ResponseEntity<List<PointRelaisDTO>> getAllPointRelais() {
        List<PointRelaisDTO> pointRelaisList = pointRelaisService.getAllPointRelais();
        return ResponseEntity.ok(pointRelaisList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointRelaisDTO> getPointRelaisById(@PathVariable Long id) {
        PointRelaisDTO pointRelais = pointRelaisService.getPointRelaisById(id);
        if (pointRelais != null) {
            return ResponseEntity.ok(pointRelais);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointRelaisDTO> updatePointRelais(@PathVariable Long id, @RequestBody PointRelaisDTO pointRelaisDTO) {
        PointRelaisDTO updatedPointRelais = pointRelaisService.updatePointRelais(id, pointRelaisDTO);
        if (updatedPointRelais != null) {
            return ResponseEntity.ok(updatedPointRelais);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePointRelais(@PathVariable Long id) {
        pointRelaisService.deletePointRelais(id);
        return ResponseEntity.noContent().build();
    }
}
