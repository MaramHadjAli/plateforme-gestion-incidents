package tn.enicarthage.plate_be.repositories;

import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.entities.ROLE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Utilisateur> findByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT u FROM Utilisateur u WHERE u.email = :email AND u.role = :role")
    Optional<Utilisateur> findByEmailAndRole(@Param("email") String email, @Param("role") ROLE role);

    Optional<Utilisateur> findByIdAndRole(Long id, ROLE role);

    List<Utilisateur> findByRole(ROLE role);


    List<Utilisateur> findByRoleAndIsActive(ROLE role, boolean isActive);

    @Query("SELECT u FROM Utilisateur u WHERE u.email = :email AND u.role = :role AND u.isActive = true")
    Optional<Utilisateur> findActiveTechnicianByEmail(@Param("email") String email, @Param("role") ROLE role);

    long countByRole(ROLE role);


    @Query("SELECT u FROM Utilisateur u WHERE u.id = :id AND u.role = 'TECHNICIEN' AND u.isActive = true")
    Optional<Utilisateur> findActiveTechnicianById(@Param("id") Long id);
}