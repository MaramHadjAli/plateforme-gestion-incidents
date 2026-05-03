package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.Notification;
import tn.enicarthage.plate_be.entities.Utilisateur;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUtilisateur(Utilisateur utilisateur);
    
    List<Notification> findByUtilisateurAndReadFalse(Utilisateur utilisateur);
    
    List<Notification> findByUtilisateurOrderByDateEnvoiDesc(Utilisateur utilisateur);
    
    @Query("SELECT n FROM Notification n WHERE n.utilisateur = :utilisateur AND n.read = false ORDER BY n.dateEnvoi DESC")
    List<Notification> findUnreadNotifications(@Param("utilisateur") Utilisateur utilisateur);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.utilisateur = :utilisateur AND n.read = false")
    long countUnreadNotifications(@Param("utilisateur") Utilisateur utilisateur);
    
    @Query("SELECT n FROM Notification n WHERE n.utilisateur = :utilisateur AND n.dateEnvoi >= :since ORDER BY n.dateEnvoi DESC")
    List<Notification> findNotificationsSince(@Param("utilisateur") Utilisateur utilisateur, @Param("since") Date since);
    
    List<Notification> findByTicketId(String ticketId);
    
    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.emailSent = false")
    List<Notification> findNotificationsPendingEmailSend(@Param("type") String type);
    
    @Query("SELECT n FROM Notification n WHERE n.severity = 'CRITICAL' AND n.smsSent = false")
    List<Notification> findCriticalNotificationsPendingSMS();
    
    void deleteByDateEnvoiBefore(Date date);
}

