package tn.enicarthage.plate_be.entities;

import java.time.LocalDateTime;

public class TraceLogin {
    private String email;
    private String action;
    private LocalDateTime date;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}