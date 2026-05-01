package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.Salle;

import java.util.List;

@Repository
public interface SalleRepository extends JpaRepository<Salle, String> {
    List<Salle> findByBatiment(String batiment);
    boolean existsByNomSalle(String nomSalle);
}