package tn.enicarthage.plate_be.repositories;

import tn.enicarthage.plate_be.entities.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnicienRepository extends JpaRepository<Technicien, Long> {

    Optional<Technicien> findByEmail(String email);

    @Query("SELECT t FROM Technicien t WHERE LOWER(t.email) = LOWER(:email)")
    Optional<Technicien> findByEmailIgnoreCase(@Param("email") String email);
}