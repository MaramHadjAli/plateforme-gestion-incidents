package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.Technicien;

import java.util.Optional;

@Repository
public interface TechnicienRepository extends JpaRepository<Technicien, Long> {
    Optional<Technicien> findByEmail(String email);
}