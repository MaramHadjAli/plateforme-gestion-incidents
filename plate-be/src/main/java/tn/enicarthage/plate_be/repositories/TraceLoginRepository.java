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
    
    
    Page<TraceLogin> findAllByOrderByDateDesc(Pageable pageable);
    
    
    Page<TraceLogin> findByEmailOrderByDateDesc(String email, Pageable pageable);
    
    
    List<TraceLogin> findByActionOrderByDateDesc(String action);
    
    
    @Query("SELECT t FROM TraceLogin t WHERE t.status = 'FAILED' ORDER BY t.date DESC")
    Page<TraceLogin> findFailedAttempts(Pageable pageable);
    
    
    @Query("SELECT t FROM TraceLogin t WHERE t.email = :email AND t.status = 'FAILED' ORDER BY t.date DESC")
    List<TraceLogin> findFailedAttemptsForEmail(@Param("email") String email);
    
    
    @Query("SELECT t FROM TraceLogin t WHERE t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    Page<TraceLogin> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    
    @Query("SELECT COUNT(t) FROM TraceLogin t WHERE t.email = :email AND t.status = 'FAILED' AND t.date > :since")
    long countRecentFailedAttempts(@Param("email") String email, @Param("since") LocalDateTime since);
}