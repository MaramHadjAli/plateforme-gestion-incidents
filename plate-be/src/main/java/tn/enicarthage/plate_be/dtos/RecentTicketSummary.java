package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecentTicketSummary {
    private String idTicket;
    private String titre;
    private String priorite;
    private String statut;
    private String dateCreation;
    private String technicien;
}

