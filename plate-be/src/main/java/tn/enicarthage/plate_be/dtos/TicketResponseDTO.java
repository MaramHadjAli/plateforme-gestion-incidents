package tn.enicarthage.plate_be.dtos;

import lombok.Data;
import tn.enicarthage.plate_be.entities.PRIORITE_TICKET;
import tn.enicarthage.plate_be.entities.STATUS_TICKET;

import java.util.Date;

@Data
public class TicketResponseDTO {
    private String idTicket;
    private String titre;
    private String description;
    private Date dateCreation;
    private Date dateLimite;
    private PRIORITE_TICKET priorite;
    private STATUS_TICKET status;

    private String demandeurNom;
}