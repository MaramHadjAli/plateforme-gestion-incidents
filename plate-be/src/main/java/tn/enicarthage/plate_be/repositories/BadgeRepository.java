package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.Badge;

/**
 * Repository pour l'entité Badge
 * Permet de gérer les opérations CRUD et les requêtes personnalisées
 */
@Repository
public interface BadgeRepository extends JpaRepository<Badge, String> {
    /**
     * Trouver un badge par son nom
     */
    Badge findByNomBadge(String nomBadge);
}

