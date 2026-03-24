package tn.enicarthage.plate_be.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.enicarthage.plate_be.entities.TraceLogin;

import java.time.LocalDateTime;
import java.util.List;

public interface TraceLoginRepository extends JpaRepository<TraceLogin, Long> {
    
    /**
     * Récupère tous les logs triés par date décroissante (plus récents d'abord)
     */
    Page<TraceLogin> findAllByOrderByDateDesc(Pageable pageable);
    
    /**
     * Récupère les logs pour un utilisateur spécifique
     */
    Page<TraceLogin> findByEmailOrderByDateDesc(String email, Pageable pageable);
    
    /**
     * Récupère les logs pour une action spécifique
     */
    List<TraceLogin> findByActionOrderByDateDesc(String action);
    
    /**
     * Récupère les logs échoués (tentatives de connexion échouées)
     */
    @Query("SELECT t FROM TraceLogin t WHERE t.status = 'FAILED' ORDER BY t.date DESC")
    Page<TraceLogin> findFailedAttempts(Pageable pageable);
    
    /**
     * Récupère les logs échoués pour un email spécifique
     */
    @Query("SELECT t FROM TraceLogin t WHERE t.email = :email AND t.status = 'FAILED' ORDER BY t.date DESC")
    List<TraceLogin> findFailedAttemptsForEmail(@Param("email") String email);
    
    /**
     * Récupère les logs entre deux dates
     */
    @Query("SELECT t FROM TraceLogin t WHERE t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    Page<TraceLogin> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    /**
     * Compte les tentatives échouées pour un email dans les dernières 15 minutes
     */
    @Query("SELECT COUNT(t) FROM TraceLogin t WHERE t.email = :email AND t.status = 'FAILED' AND t.date > :since")
    long countRecentFailedAttempts(@Param("email") String email, @Param("since") LocalDateTime since);
}