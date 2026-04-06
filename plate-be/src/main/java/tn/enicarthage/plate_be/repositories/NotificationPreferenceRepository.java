package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.NotificationPreference;
import tn.enicarthage.plate_be.entities.Utilisateur;

import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    Optional<NotificationPreference> findByUtilisateur(Utilisateur utilisateur);
}

