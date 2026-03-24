package tn.enicarthage.plate_be.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tn.enicarthage.plate_be.dtos.NotificationMessage;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = event.getUser().getName();
        if (username != null) {
            NotificationMessage notification = new NotificationMessage(
                    "USER_DISCONNECTED",
                    "Utilisateur déconnecté",
                    username + " s'est déconnecté",
                    null,
                    "SYSTEM",
                    "INFO"
            );
            messagingTemplate.convertAndSend("/topic/admin", notification);
        }
    }
}
