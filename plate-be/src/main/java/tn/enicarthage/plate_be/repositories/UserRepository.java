package tn.enicarthage.plate_be.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.entities.Utilisateur;

public interface UserRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    long countByRole(ROLE role);
}
