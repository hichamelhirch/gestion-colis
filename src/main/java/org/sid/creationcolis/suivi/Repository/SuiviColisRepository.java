package org.sid.creationcolis.suivi.Repository;

import org.sid.creationcolis.entities.Colis;
import org.sid.creationcolis.suivi.Entity.SuiviColis;
import org.sid.creationcolis.suivi.enums.StatutSuiviColis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuiviColisRepository extends JpaRepository<SuiviColis, Long> {
  List<SuiviColis> findByColisId(Long colisId);
  List<SuiviColis> findByColis(Colis colis);
  Optional<SuiviColis> findTopByColisOrderByDateSuiviDesc(Colis colis);
    List<SuiviColis> findByStatut(StatutSuiviColis statut);

}
