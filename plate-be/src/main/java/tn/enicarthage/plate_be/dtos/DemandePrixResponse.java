package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandePrixResponse {
    private String ticketId;
    private String ticketTitle;
    private Date deadline;
    private boolean sent;
    private int techniciansNotified;
    private String message;
}
