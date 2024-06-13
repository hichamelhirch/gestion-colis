package org.sid.creationcolis.service;

import org.sid.creationcolis.dtos.PointRelaisDTO;
import org.sid.creationcolis.entities.PointRelais;
import org.sid.creationcolis.mappers.PointRelaisMapper;
import org.sid.creationcolis.repository.PointRelaisRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointRelaisService {

    private final PointRelaisRepository pointRelaisRepository;
    private final PointRelaisMapper pointRelaisMapper;

    public PointRelaisService(PointRelaisRepository pointRelaisRepository, PointRelaisMapper pointRelaisMapper) {
        this.pointRelaisRepository = pointRelaisRepository;
        this.pointRelaisMapper = pointRelaisMapper;
    }

    public List<PointRelaisDTO> getAllPointRelais() {
        return pointRelaisRepository.findAll().stream()
                .map(pointRelaisMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PointRelaisDTO getPointRelaisById(Long id) {
        PointRelais pointRelais = pointRelaisRepository.findById(id).orElse(null);
        if (pointRelais != null) {
            return pointRelaisMapper.toDTO(pointRelais);
        }
        return null;
    }

    public PointRelaisDTO savePointRelais(PointRelaisDTO dto) {
        PointRelais pointRelais = pointRelaisMapper.toEntity(dto);
        PointRelais savedPointRelais = pointRelaisRepository.save(pointRelais);
        return pointRelaisMapper.toDTO(savedPointRelais);
    }

    public PointRelaisDTO updatePointRelais(Long id, PointRelaisDTO dto) {
        PointRelais pointRelais = pointRelaisRepository.findById(id).orElse(null);
        if (pointRelais != null) {

            BeanUtils.copyProperties(dto, pointRelais, "id");
            PointRelais updatedPointRelais = pointRelaisRepository.save(pointRelais);
            return pointRelaisMapper.toDTO(updatedPointRelais);
        }
        return null;
    }

    public void deletePointRelais(Long id) {
        pointRelaisRepository.deleteById(id);
    }
}
