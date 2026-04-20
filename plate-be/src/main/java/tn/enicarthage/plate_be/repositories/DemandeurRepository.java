package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.plate_be.entities.Demandeur;

import java.util.Optional;

public interface DemandeurRepository extends JpaRepository<Demandeur, String> {

    Demandeur findByEmail(String email);

}