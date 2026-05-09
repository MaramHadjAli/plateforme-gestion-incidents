package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentTicketSummary {
    private String idTicket;
    private String titre;
    private String priorite;
    private String status;
    private String dateCreation;
    private String technicienNom;
}
