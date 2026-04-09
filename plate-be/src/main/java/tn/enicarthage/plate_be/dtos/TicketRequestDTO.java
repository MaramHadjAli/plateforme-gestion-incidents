package tn.enicarthage.plate_be.dtos;

import lombok.Data;
import tn.enicarthage.plate_be.entities.PRIORITE_TICKET;

import java.util.Date;

@Data
public class TicketRequestDTO {
    private String titre;
    private String description;
    private PRIORITE_TICKET priorite;
    private Date dateLimite;
    private String demandeurId;
}