package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    private String ticketId;
    private String sender;
    private String id;
    private String type;
    private String title;
    private String message;
    private LocalDateTime timestamp;
    private String severity;


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
