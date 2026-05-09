package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.Badge;


@Repository
public interface BadgeRepository extends JpaRepository<Badge, String> {
    
    Badge findByNomBadge(String nomBadge);
}

