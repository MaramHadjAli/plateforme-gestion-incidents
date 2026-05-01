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
    private Date dateCloture;
    private Date dateLimite;
    private PRIORITE_TICKET priorite;
    private STATUS_TICKET status;

    private String demandeurNom;
    private String demandeurEmail;
    private String technicienNom;
    private Long technicienId;
    
    private String idSalle;
    private String nomSalle;
    private String idEquipement;
    private String nomEquipement;
}