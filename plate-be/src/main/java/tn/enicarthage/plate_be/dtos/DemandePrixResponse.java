package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandePrixResponse {
    private String ticketId;
    private String ticketTitle;
    private Date deadline;
    private boolean sent;
    private int techniciansNotified;
    private String message;
}