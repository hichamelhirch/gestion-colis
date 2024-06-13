package org.sid.creationcolis.repository;

import org.sid.creationcolis.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface RegionRepository extends JpaRepository<Region,Long> {

}
