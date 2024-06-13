package org.sid.creationcolis.repository;

import org.sid.creationcolis.entities.ServiceALivraison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceALivraisonRepository extends JpaRepository<ServiceALivraison,Long> {
    List<ServiceALivraison> findAllById(Iterable<Long> ids);
}
