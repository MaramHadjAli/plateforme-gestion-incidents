package tn.enicarthage.plate_be.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.NotificationMessage;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
}
