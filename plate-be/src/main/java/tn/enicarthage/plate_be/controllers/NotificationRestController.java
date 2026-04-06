package tn.enicarthage.plate_be.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.NotificationMessage;
import tn.enicarthage.plate_be.entities.Notification;
import tn.enicarthage.plate_be.entities.NotificationPreference;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.repositories.UserRepository;
import tn.enicarthage.plate_be.websocket.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationRestController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    // ==================== WEBSOCKET METHODS ====================

    @PostMapping("/send-to-admin")
    public ResponseEntity<String> sendToAdmin(@RequestBody NotificationMessage notification) {
        notificationService.notifyAdmin(notification);
        return ResponseEntity.ok("Notification envoyée aux administrateurs");
    }

    @PostMapping("/send-to-user")
    public ResponseEntity<String> sendToUser(@RequestBody NotificationMessage notification) {
        notificationService.notifyUser(notification.getTicketId(), notification);
        return ResponseEntity.ok("Notification envoyée à l'utilisateur");
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcast(@RequestBody NotificationMessage notification) {
        notificationService.broadcastNotification(notification);
        return ResponseEntity.ok("Notification envoyée à tous");
    }

    // ==================== GET NOTIFICATIONS ====================

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(
            @RequestParam(defaultValue = "20") int limit) {
        Utilisateur currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Notification> notifications = notificationService.getUserNotifications(currentUser, limit);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        Utilisateur currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(currentUser);
        return ResponseEntity.ok(unreadNotifications);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Utilisateur currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        long count = notificationService.getUnreadNotificationCount(currentUser);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<Notification>> getNotificationsByTicket(@PathVariable String ticketId) {
        List<Notification> notifications = notificationService.getNotificationsByTicket(ticketId);
        return ResponseEntity.ok(notifications);
    }

    // ==================== UPDATE NOTIFICATIONS ====================

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        Utilisateur currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok().build();
    }

    // ==================== DELETE NOTIFICATIONS ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/old/{daysOld}")
    public ResponseEntity<Void> deleteOldNotifications(@PathVariable int daysOld) {
        notificationService.deleteOldNotifications(daysOld);
        return ResponseEntity.ok().build();
    }

    // ==================== PREFERENCES ====================

    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreference> getPreferences() {
        Utilisateur currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        NotificationPreference preference = notificationService.getOrCreatePreference(currentUser);
        return ResponseEntity.ok(preference);
    }

    @PutMapping("/preferences")
    public ResponseEntity<NotificationPreference> updatePreferences(
            @RequestBody NotificationPreference updatedPreferences) {
        Utilisateur currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        NotificationPreference preference = notificationService.updatePreference(currentUser, updatedPreferences);
        return ResponseEntity.ok(preference);
    }

    // ==================== HELPER ====================

    private Utilisateur getCurrentUser() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmail(username).orElse(null);
        } catch (Exception e) {
            log.error("Error getting current user", e);
            return null;
        }
    }
}
