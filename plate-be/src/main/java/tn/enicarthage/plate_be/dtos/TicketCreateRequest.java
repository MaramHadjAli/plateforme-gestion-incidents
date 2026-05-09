package tn.enicarthage.plate_be.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketCreateRequest {
    private String titre;
    private String description;
    private String priorite;
    private String idSalle;
    private String idEquipement;
    private String typePanne;
    private String dateLimiteReparation;
}

