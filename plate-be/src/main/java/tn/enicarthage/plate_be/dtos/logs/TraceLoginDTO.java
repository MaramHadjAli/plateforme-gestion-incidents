package tn.enicarthage.plate_be.dtos.logs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TraceLoginDTO {
    private Long id;
    private String email;
    private String action;
    private String status;
    private LocalDateTime date;
    private String ipAddress;
    private String userAgent;
    private String details;
}

