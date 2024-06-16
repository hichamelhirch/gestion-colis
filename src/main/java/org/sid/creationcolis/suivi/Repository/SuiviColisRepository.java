package org.sid.creationcolis.suivi.Repository;

import org.sid.creationcolis.entities.Colis;
import org.sid.creationcolis.suivi.Entity.SuiviColis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuiviColisRepository extends JpaRepository<SuiviColis, Long> {
  List<SuiviColis> findByColisId(Long colisId);
  List<SuiviColis> findByColis(Colis colis);

}
