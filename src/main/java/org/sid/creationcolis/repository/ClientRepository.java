package org.sid.creationcolis.repository;

import org.sid.creationcolis.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {
    @Query("SELECT c FROM Client c JOIN User u ON c.id = u.client.id WHERE u.id = :userId")
    Optional<Client> findByUserId(@Param("userId") Long userId);
  //public   Client  findByEmail(String email);
  // public Client findByPassword(String password);
   // boolean existsByEmail(String email);


}

