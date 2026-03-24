package tn.enicarthage.plate_be.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.enicarthage.plate_be.dtos.NotificationMessage;
import tn.enicarthage.plate_be.websocket.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    @Autowired
    private NotificationService notificationService;

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
}
