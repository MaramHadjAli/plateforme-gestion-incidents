package tn.enicarthage.plate_be.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class NotificationMessage {

    @Getter
    private String ticketId;
    @Setter
    @Getter
    private String sender;


    @Getter @Setter
    private String id;
    @Getter @Setter
    private String type;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private String message;
    @Getter @Setter
    private LocalDateTime timestamp;
    @Getter @Setter
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
