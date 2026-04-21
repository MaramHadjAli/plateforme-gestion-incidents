package tn.enicarthage.plate_be.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.plate_be.entities.Equipement;

public interface EquipementRepository extends JpaRepository<Equipement, String> {
    List<Equipement> findBySalle_IdSalle(String idSalle);
}

