package org.sid.creationcolis.repository;

import org.sid.creationcolis.entities.Colis;
import org.sid.creationcolis.entities.Ville;
import org.sid.creationcolis.enums.StatutColis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ColisRepository extends JpaRepository<Colis,Long> {
    List<Colis> findByStatusColis(StatutColis status);
   // List<Colis> findByVilleDepart(Ville villeDepart);
    List<Colis> findByVilleDestinataire(Ville villeDestinataire);
    @Query("SELECT c FROM Colis c WHERE c.villeDestinataire.id = ?1 AND c.statusColis = ?2")
    List<Colis> findByVilleDestinataireAndStatusColis(Long villeDestinataire, StatutColis status);
    Optional<Colis> findByNumeroColis(String numeroColis);
  //  boolean existsByCodeBarre(String codeBarre);
  boolean existsByCodeBarre(String codeBarre);

    Optional<Colis> findByCodeBarre(String codeBarre);

    List<Colis> findByStatusColisIn(List<StatutColis> statusColisList);
}
