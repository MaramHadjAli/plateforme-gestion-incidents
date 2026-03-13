package tn.enicarthage.plate_be.entities;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Ticket {
    private String idTicket;
    private String titre;
    private String description;
    private Date dateCreation;
    private Date dateCloture;
    private PRIORITE_TICKET priorite;
    private STATUS_TICKET status;
    private Date dateLimiteReparation;
}