package org.sid.creationcolis.suivi.Repository;


import org.sid.creationcolis.entities.Colis;
import org.sid.creationcolis.suivi.Entity.SuiviColisHistorique;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuiviColisHistoriqueRepository extends JpaRepository<SuiviColisHistorique, Long> {
    List<SuiviColisHistorique> findByColis(Colis colis);
}

