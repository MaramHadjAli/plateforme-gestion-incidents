package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.plate_be.entities.RefreshToken;
import tn.enicarthage.plate_be.entities.Utilisateur;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(Utilisateur user);

    Optional<RefreshToken> findByUser(Utilisateur user);}
