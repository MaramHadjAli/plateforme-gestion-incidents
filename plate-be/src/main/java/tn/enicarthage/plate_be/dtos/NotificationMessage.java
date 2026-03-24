package tn.enicarthage.plate_be.dtos;

import java.time.LocalDateTime;

public class NotificationMessage {
    private String id;
    private String type;
    private String title;
    private String message;
    private String ticketId;
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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
