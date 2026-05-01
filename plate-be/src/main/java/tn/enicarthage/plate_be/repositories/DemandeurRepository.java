package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.Demandeur;

import java.util.Optional;

@Repository
public interface DemandeurRepository extends JpaRepository<Demandeur, Long> {

    Demandeur findByEmail(String email);
}