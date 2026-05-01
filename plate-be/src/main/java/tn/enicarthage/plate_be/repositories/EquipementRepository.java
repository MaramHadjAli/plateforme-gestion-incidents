package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.ETAT_EQUIPEMENT;
import tn.enicarthage.plate_be.entities.Equipement;

import java.util.Date;
import java.util.List;

@Repository
public interface EquipementRepository extends JpaRepository<Equipement, String> {

    List<Equipement> findBySalle_IdSalle(String idSalle);

    List<Equipement> findByEtat(ETAT_EQUIPEMENT etat);

    List<Equipement> findByDateFinGarantieBefore(Date date);

    long countByEtat(ETAT_EQUIPEMENT etat);
}