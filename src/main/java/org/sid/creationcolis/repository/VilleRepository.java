package org.sid.creationcolis.repository;

import org.sid.creationcolis.entities.Hub;
import org.sid.creationcolis.entities.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface VilleRepository extends JpaRepository<Ville, Long> {

    @Query("SELECT v.region.zone FROM Ville v WHERE v = :ville")
    String findZoneByVille(@Param("ville") Ville ville);

    List<Ville> findByVilleContaining(String query);

    Ville findByVille(String ville);




    @Query("SELECT v.hubVille FROM Ville v WHERE v.id = :villeId")
    Hub findHubByVilleId(@Param("villeId") Long villeId);

    @Query("SELECT v.ville FROM Ville v WHERE lower(v.ville) LIKE %:villeSearch%")
    List<String> searchVilleNamesIgnoreCase(String villeSearch);


}
