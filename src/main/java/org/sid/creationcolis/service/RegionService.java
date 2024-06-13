package org.sid.creationcolis.service;

import org.sid.creationcolis.dtos.RegionDTO;
import org.sid.creationcolis.mappers.RegionMapper;
import org.sid.creationcolis.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    @Autowired
    public RegionService(RegionRepository regionRepository, RegionMapper regionMapper) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper;
    }

    public List<RegionDTO> getAllRegions() {
        return regionRepository.findAll().stream()
                .map(regionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
