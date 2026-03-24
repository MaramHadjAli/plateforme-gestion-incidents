package tn.enicarthage.plate_be.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class NotificationMessage {
    private String id;
    private String type;
    private String title;
    private String message;
    @Getter
    private String ticketId;
    @Setter
    @Getter
    private String sender;
    private LocalDateTime timestamp;
    private String severity;

    public NotificationMessage() {}

    public NotificationMessage(String type, String title, String message, String ticketId, String sender, String severity) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.ticketId = ticketId;
        this.sender = sender;
        this.severity = severity;
        this.timestamp = LocalDateTime.now();
    }

}
