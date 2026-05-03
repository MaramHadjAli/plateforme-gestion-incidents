package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.entities.Utilisateur;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByRole(ROLE role);
    Optional<Utilisateur> findByEmailAndRole(String email, ROLE role);
    Optional<Utilisateur> findByIdAndRole(Long id, ROLE role);
    Optional<Utilisateur> findByConfirmationToken(String token);
    long countByRole(ROLE role);
}
