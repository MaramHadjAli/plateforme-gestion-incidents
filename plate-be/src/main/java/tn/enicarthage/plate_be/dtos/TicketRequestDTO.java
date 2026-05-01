package tn.enicarthage.plate_be.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class TicketRequestDTO {
    private String titre;
    private String description;
    private String priorite;   // String to avoid 400 on unknown values
    private Date dateLimite;
    private String idSalle;
    private String idEquipement;
    private String demandeurId;
}