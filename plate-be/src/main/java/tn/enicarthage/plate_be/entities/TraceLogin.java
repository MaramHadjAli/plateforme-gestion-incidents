package tn.enicarthage.plate_be.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TraceLogin {
    private String email;
    private String action;
    private LocalDateTime date;

}