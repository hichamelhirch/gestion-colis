package org.sid.creationcolis.repository;

import org.sid.creationcolis.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
   Optional<User> findByClientId(Long clientId);

}
