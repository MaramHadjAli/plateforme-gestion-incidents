package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.plate_be.entities.Salle;

public interface SalleRepository extends JpaRepository<Salle, String> {
}

