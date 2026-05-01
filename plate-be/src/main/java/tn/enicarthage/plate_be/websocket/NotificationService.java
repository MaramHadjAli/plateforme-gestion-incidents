package tn.enicarthage.plate_be.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.NotificationMessage;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.NotificationRepository;
import tn.enicarthage.plate_be.repositories.NotificationPreferenceRepository;
import tn.enicarthage.plate_be.services.EmailService;
import tn.enicarthage.plate_be.services.SMSService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;


    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

    // ==================== WEBSOCKET NOTIFICATIONS ====================

    public void notifyAdmin(NotificationMessage notification) {
        messagingTemplate.convertAndSend("/topic/admin", notification);
    }

    public void notifyTechnician(String technicianId, NotificationMessage notification) {
        messagingTemplate.convertAndSendToUser(technicianId, "/queue/notifications", notification);
    }

    public void notifyUser(String userId, NotificationMessage notification) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }

    public void broadcastNotification(NotificationMessage notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    // ==================== NOTIFICATION CREATION ====================

    public Notification createAndSendNotification(Utilisateur utilisateur, String type, String title, 
                                                   String message, String severity, String ticketId) {
        if (utilisateur == null) {
            log.warn("Cannot create notification: Utilisateur is null");
            return null;
        }

        // Check user preferences
        NotificationPreference preference = preferenceRepository.findByUtilisateur(utilisateur).orElse(null);
        if (preference != null && !shouldNotify(preference, type)) {
            log.debug("Notification skipped due to user preferences: {}", type);
            return null;
        }

        // Create notification entity
        Notification notification = Notification.builder()
                .type(type)
                .title(title)
                .message(message)
                .severity(severity)
                .ticketId(ticketId)
                .utilisateur(utilisateur)
                .read(false)
                .emailSent(false)
                .smsSent(false)
                .dateEnvoi(new Date())
                .build();

        notification = notificationRepository.save(notification);

        // Send WebSocket notification
        NotificationMessage wsMessage = new NotificationMessage(type, title, message, ticketId, "SYSTEM", severity);
        notifyUser(utilisateur.getId().toString(), wsMessage);

        // Send based on preferences
        if (preference != null) {
            if (preference.isPushNotificationEnabled()) {
                // WebSocket already sent above
                log.info("Push notification sent for user {}", utilisateur.getId());
            }
            if (preference.isEmailEnabled()) {
                emailService.sendNotificationEmail(notification);
            }
            if (preference.isSmsEnabled() && "CRITICAL".equals(severity)) {
                smsService.sendCriticalNotificationSMS(notification);
            }
        } else {
            // Default: send email for all, SMS only for critical
            emailService.sendNotificationEmail(notification);
            if ("CRITICAL".equals(severity)) {
                smsService.sendCriticalNotificationSMS(notification);
            }
        }

        log.info("Notification created and sent: {} for user {}", type, utilisateur.getId());
        return notification;
    }

    // ==================== TICKET NOTIFICATIONS ====================

    public void notifyTicketAssigned(Utilisateur assignee, String ticketId, String ticketTitle) {
        String message = String.format("Vous avez été assigné au ticket: %s", ticketTitle);
        createAndSendNotification(assignee, "TICKET_ASSIGNED", "Nouveau Ticket Assigné", 
                                 message, "INFO", ticketId);
    }

    public void notifyStatusChanged(Utilisateur user, String ticketId, String newStatus) {
        String message = String.format("Le statut du ticket %s a changé à: %s", ticketId, newStatus);
        createAndSendNotification(user, "STATUS_CHANGED", "Changement de Statut", 
                                 message, "WARNING", ticketId);
    }

    public void notifyMaintenanceReminder(Utilisateur user, String equipmentName, Date maintenanceDate) {
        String message = String.format("Rappel: Maintenance préventive prévue pour %s le %s", 
                                       equipmentName, maintenanceDate);
        createAndSendNotification(user, "MAINTENANCE_REMINDER", "Rappel de Maintenance", 
                                 message, "WARNING", null);
    }

    public void notifyBadgeAwarded(Utilisateur user, String badgeName, String badgeDescription) {
        String message = String.format("Félicitations! Vous avez reçu le badge: %s - %s", 
                                       badgeName, badgeDescription);
        createAndSendNotification(user, "BADGE_AWARDED", "🏆 Nouveau Badge", 
                                 message, "INFO", null);
    }

    public void notifySLAExceeded(Utilisateur user, String ticketId, String ticketTitle) {
        String message = String.format("⚠️ Le SLA du ticket %s (%s) a été DÉPASSÉ! Action immédiate requise!", 
                                       ticketId, ticketTitle);
        Notification notification = createAndSendNotification(user, "SLA_EXCEEDED", 
                                                              "🚨 SLA DÉPASSÉ!", message, 
                                                              "CRITICAL", ticketId);
        
        // Send SMS alert for critical SLA breach
        if (notification != null && user.getTelephone() != null) {
            smsService.sendSLAExceededSMS(user.getTelephone(), ticketId);
        }
    }

    // ==================== NOTIFICATION RETRIEVAL ====================

    public List<Notification> getUserNotifications(Utilisateur utilisateur, int limit) {
        return notificationRepository.findByUtilisateurOrderByDateEnvoiDesc(utilisateur)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Notification> getUnreadNotifications(Utilisateur utilisateur) {
        return notificationRepository.findUnreadNotifications(utilisateur);
    }

    public long getUnreadNotificationCount(Utilisateur utilisateur) {
        return notificationRepository.countUnreadNotifications(utilisateur);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public List<Notification> getNotificationsByTicket(String ticketId) {
        return notificationRepository.findByTicketId(ticketId);
    }

    // ==================== NOTIFICATION MANAGEMENT ====================

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
            log.debug("Notification marked as read: {}", notificationId);
        });
    }

    public void markAllAsRead(Utilisateur utilisateur) {
        List<Notification> unread = notificationRepository.findUnreadNotifications(utilisateur);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        log.info("All notifications marked as read for user: {}", utilisateur.getId());
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        log.debug("Notification deleted: {}", notificationId);
    }

    public void deleteOldNotifications(int daysOld) {
        Date threshold = new Date(System.currentTimeMillis() - (long) daysOld * 24 * 60 * 60 * 1000);
        notificationRepository.deleteByDateEnvoiBefore(threshold);
        log.info("Old notifications deleted (older than {} days)", daysOld);
    }

    // ==================== DAILY DIGEST ====================

    public void sendDailyDigest(Utilisateur utilisateur) {
        List<Notification> todayNotifications = notificationRepository
                .findNotificationsSince(utilisateur, getTodayStart());

        if (todayNotifications.isEmpty()) {
            log.debug("No notifications to digest for user: {}", utilisateur.getId());
            return;
        }

        StringBuilder digestContent = new StringBuilder();
        for (Notification notif : todayNotifications) {
            digestContent.append("<div class='notification-item'>")
                    .append("<strong>").append(notif.getTitle()).append("</strong><br>")
                    .append("<small>").append(notif.getType()).append(" - ")
                    .append(notif.getDateEnvoi()).append("</small><br>")
                    .append(notif.getMessage())
                    .append("</div>");
        }

        emailService.sendDailyDigest(utilisateur.getEmail(), digestContent.toString(), 
                                    todayNotifications.size());
        log.info("Daily digest sent to user: {}", utilisateur.getId());
    }

    private Date getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    // ==================== PREFERENCES ====================

    public NotificationPreference getOrCreatePreference(Utilisateur utilisateur) {
        return preferenceRepository.findByUtilisateur(utilisateur)
                .orElseGet(() -> {
                    NotificationPreference pref = NotificationPreference.builder()
                            .utilisateur(utilisateur)
                            .ticketAssignedEnabled(true)
                            .statusChangedEnabled(true)
                            .maintenanceReminderEnabled(true)
                            .badgeAwardedEnabled(true)
                            .slaExceededEnabled(true)
                            .pushNotificationEnabled(true)
                            .emailEnabled(true)
                            .smsEnabled(false)
                            .emailDigestFrequency("DAILY")
                            .smsCriticalOnly(true)
                            .build();
                    return preferenceRepository.save(pref);
                });
    }

    public NotificationPreference updatePreference(Utilisateur utilisateur, NotificationPreference updatedPrefs) {
        NotificationPreference pref = getOrCreatePreference(utilisateur);
        // Update fields
        pref.setTicketAssignedEnabled(updatedPrefs.isTicketAssignedEnabled());
        pref.setStatusChangedEnabled(updatedPrefs.isStatusChangedEnabled());
        pref.setMaintenanceReminderEnabled(updatedPrefs.isMaintenanceReminderEnabled());
        pref.setBadgeAwardedEnabled(updatedPrefs.isBadgeAwardedEnabled());
        pref.setSlaExceededEnabled(updatedPrefs.isSlaExceededEnabled());
        pref.setPushNotificationEnabled(updatedPrefs.isPushNotificationEnabled());
        pref.setEmailEnabled(updatedPrefs.isEmailEnabled());
        pref.setSmsEnabled(updatedPrefs.isSmsEnabled());
        pref.setEmailDigestFrequency(updatedPrefs.getEmailDigestFrequency());
        pref.setSmsCriticalOnly(updatedPrefs.isSmsCriticalOnly());
        pref.setQuietHoursStart(updatedPrefs.getQuietHoursStart());
        pref.setQuietHoursEnd(updatedPrefs.getQuietHoursEnd());
        return preferenceRepository.save(pref);
    }

    private boolean shouldNotify(NotificationPreference preference, String type) {
        if (!preference.isAllNotificationsEnabled()) {
            return false;
        }

        return switch (type) {
            case "TICKET_ASSIGNED" -> preference.isTicketAssignedEnabled();
            case "STATUS_CHANGED" -> preference.isStatusChangedEnabled();
            case "MAINTENANCE_REMINDER" -> preference.isMaintenanceReminderEnabled();
            case "BADGE_AWARDED" -> preference.isBadgeAwardedEnabled();
            case "SLA_EXCEEDED" -> preference.isSlaExceededEnabled();
            default -> true;
        };
    }
}
