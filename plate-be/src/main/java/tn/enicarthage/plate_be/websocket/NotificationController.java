package tn.enicarthage.plate_be.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import tn.enicarthage.plate_be.dtos.NotificationMessage;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/sendNotification")
    @SendTo("/topic/notifications")
    public NotificationMessage sendNotification(NotificationMessage notification, SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser().getName();
        notification.setSender(username);
        return notification;
    }

    @MessageMapping("/notifyAdmin")
    public void notifyAdmin(NotificationMessage notification, SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser().getName();
        notification.setSender(username);
        notificationService.notifyAdmin(notification);
    }

    @MessageMapping("/notifyTechnician")
    public void notifyTechnician(NotificationMessage notification, SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser().getName();
        notification.setSender(username);
        notificationService.notifyTechnician(notification.getTicketId(), notification);
    }
}
