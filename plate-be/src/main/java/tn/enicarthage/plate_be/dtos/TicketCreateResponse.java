package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TicketCreateResponse {
    private String idTicket;
    private String dateCreation;
    private String dateLimiteReparation;
    private String status;
}

